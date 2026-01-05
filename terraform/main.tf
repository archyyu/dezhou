# Terraform configuration for TexasHolder Poker AWS Deployment
# This configuration sets up a complete VPC, security groups, and EC2 instances
# for running the TexasHolder Poker application with Docker

terraform {
  required_version = ">= 1.0.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

# Configure the AWS Provider
provider "aws" {
  region = var.aws_region
}

# Create a VPC for the TexasHolder Poker application
resource "aws_vpc" "texasholder_vpc" {
  cidr_block           = var.vpc_cidr_block
  enable_dns_support   = true
  enable_dns_hostnames = true
  instance_tenancy     = "default"

  tags = {
    Name        = "texasholder-poker-vpc"
    Environment = var.environment
    Project     = "texasholder-poker"
  }
}

# Create public subnets in multiple availability zones
resource "aws_subnet" "public_subnets" {
  count                   = length(var.public_subnet_cidrs)
  vpc_id                  = aws_vpc.texasholder_vpc.id
  cidr_block              = var.public_subnet_cidrs[count.index]
  availability_zone       = var.availability_zones[count.index]
  map_public_ip_on_launch = true

  tags = {
    Name        = "texasholder-public-subnet-${count.index}"
    Environment = var.environment
    Project     = "texasholder-poker"
  }
}

# Create private subnets for database
resource "aws_subnet" "private_subnets" {
  count             = length(var.private_subnet_cidrs)
  vpc_id            = aws_vpc.texasholder_vpc.id
  cidr_block        = var.private_subnet_cidrs[count.index]
  availability_zone = var.availability_zones[count.index]

  tags = {
    Name        = "texasholder-private-subnet-${count.index}"
    Environment = var.environment
    Project     = "texasholder-poker"
  }
}

# Create Internet Gateway for public subnets
resource "aws_internet_gateway" "texasholder_igw" {
  vpc_id = aws_vpc.texasholder_vpc.id

  tags = {
    Name        = "texasholder-internet-gateway"
    Environment = var.environment
    Project     = "texasholder-poker"
  }
}

# Create NAT Gateway for private subnets
resource "aws_eip" "nat_eip" {
  domain = "vpc"

  tags = {
    Name        = "texasholder-nat-eip"
    Environment = var.environment
    Project     = "texasholder-poker"
  }
}

resource "aws_nat_gateway" "texasholder_nat" {
  allocation_id = aws_eip.nat_eip.id
  subnet_id     = aws_subnet.public_subnets[0].id

  tags = {
    Name        = "texasholder-nat-gateway"
    Environment = var.environment
    Project     = "texasholder-poker"
  }

  depends_on = [aws_internet_gateway.texasholder_igw]
}

# Create route tables
resource "aws_route_table" "public_route_table" {
  vpc_id = aws_vpc.texasholder_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.texasholder_igw.id
  }

  tags = {
    Name        = "texasholder-public-route-table"
    Environment = var.environment
    Project     = "texasholder-poker"
  }
}

resource "aws_route_table" "private_route_table" {
  vpc_id = aws_vpc.texasholder_vpc.id

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.texasholder_nat.id
  }

  tags = {
    Name        = "texasholder-private-route-table"
    Environment = var.environment
    Project     = "texasholder-poker"
  }
}

# Associate route tables with subnets
resource "aws_route_table_association" "public_subnet_association" {
  count          = length(var.public_subnet_cidrs)
  subnet_id      = aws_subnet.public_subnets[count.index].id
  route_table_id = aws_route_table.public_route_table.id
}

resource "aws_route_table_association" "private_subnet_association" {
  count          = length(var.private_subnet_cidrs)
  subnet_id      = aws_subnet.private_subnets[count.index].id
  route_table_id = aws_route_table.private_route_table.id
}

# Create security groups
resource "aws_security_group" "backend_sg" {
  name        = "texasholder-backend-sg"
  description = "Security group for TexasHolder Poker backend servers"
  vpc_id      = aws_vpc.texasholder_vpc.id

  # Inbound rules
  ingress {
    description = "HTTP from anywhere"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "SSH from anywhere"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "WebSocket from anywhere"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Outbound rules
  egress {
    description = "All outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "texasholder-backend-sg"
    Environment = var.environment
    Project     = "texasholder-poker"
  }
}

resource "aws_security_group" "frontend_sg" {
  name        = "texasholder-frontend-sg"
  description = "Security group for TexasHolder Poker frontend servers"
  vpc_id      = aws_vpc.texasholder_vpc.id

  # Inbound rules
  ingress {
    description = "HTTP from anywhere"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "HTTPS from anywhere"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "SSH from anywhere"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Outbound rules
  egress {
    description = "All outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "texasholder-frontend-sg"
    Environment = var.environment
    Project     = "texasholder-poker"
  }
}

resource "aws_security_group" "database_sg" {
  name        = "texasholder-database-sg"
  description = "Security group for TexasHolder Poker database"
  vpc_id      = aws_vpc.texasholder_vpc.id

  # Inbound rules - only allow traffic from backend
  ingress {
    description     = "MySQL from backend"
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [aws_security_group.backend_sg.id]
  }

  # Outbound rules
  egress {
    description = "All outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "texasholder-database-sg"
    Environment = var.environment
    Project     = "texasholder-poker"
  }
}

# Create RDS MySQL database
resource "aws_db_subnet_group" "texasholder_db_subnet_group" {
  name       = "texasholder-db-subnet-group"
  subnet_ids = aws_subnet.private_subnets[*].id

  tags = {
    Name        = "texasholder-db-subnet-group"
    Environment = var.environment
    Project     = "texasholder-poker"
  }
}

resource "aws_db_instance" "texasholder_mysql" {
  identifier             = "texasholder-mysql"
  engine                 = "mysql"
  engine_version         = "8.0"
  instance_class         = var.db_instance_class
  allocated_storage      = var.db_allocated_storage
  storage_type           = "gp2"
  db_name                = var.db_name
  username               = var.db_username
  password               = var.db_password
  parameter_group_name   = "default.mysql8.0"
  skip_final_snapshot    = true
  publicly_accessible    = false
  vpc_security_group_ids = [aws_security_group.database_sg.id]
  db_subnet_group_name   = aws_db_subnet_group.texasholder_db_subnet_group.name
  multi_az               = var.db_multi_az

  tags = {
    Name        = "texasholder-mysql"
    Environment = var.environment
    Project     = "texasholder-poker"
  }
}

# IAM Resources for EC2 instances

# IAM Role for EC2 instances
resource "aws_iam_role" "texasholder_ec2_role" {
  name = "texasholder-ec2-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })

  tags = {
    Name        = "texasholder-ec2-role"
    Environment = var.environment
    Project     = "texasholder-poker"
  }
}

# Instance Profile
resource "aws_iam_instance_profile" "texasholder_instance_profile" {
  name = "texasholder-instance-profile"
  role = aws_iam_role.texasholder_ec2_role.name
}

# Basic EC2 Policy with minimum required permissions
resource "aws_iam_role_policy" "texasholder_ec2_policy" {
  name = "texasholder-ec2-policy"
  role = aws_iam_role.texasholder_ec2_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "ec2:DescribeInstances",
          "ec2:DescribeTags",
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents",
          "logs:DescribeLogStreams",
          "ssm:GetParameter",
          "ssm:GetParameters"
        ],
        Resource = "*"
      }
    ]
  })
}

# RDS Access Policy for backend instances
resource "aws_iam_role_policy" "texasholder_rds_access_policy" {
  name = "texasholder-rds-access-policy"
  role = aws_iam_role.texasholder_ec2_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "rds:DescribeDBInstances",
          "rds:ListTagsForResource"
        ],
        Resource = "*"
      }
    ]
  })
}

# Create EC2 instances for backend
resource "aws_instance" "backend_instances" {
  count                  = var.backend_instance_count
  ami                    = var.ami_id
  instance_type          = var.backend_instance_type
  subnet_id              = aws_subnet.public_subnets[count.index % length(aws_subnet.public_subnets)].id
  vpc_security_group_ids = [aws_security_group.backend_sg.id]
  key_name               = var.ssh_key_name
  iam_instance_profile   = aws_iam_instance_profile.texasholder_instance_profile.name

  tags = {
    Name        = "texasholder-backend-${count.index}"
    Environment = var.environment
    Project     = "texasholder-poker"
    Role        = "backend"
  }

  user_data = <<-EOF
              #!/bin/bash
              sudo apt-get update -y
              sudo apt-get install -y docker.io docker-compose
              sudo systemctl enable docker
              sudo systemctl start docker
              sudo usermod -aG docker ubuntu
              
              # Clone the repository (replace with your actual repo)
              git clone https://github.com/yourusername/texasholder-poker.git /home/ubuntu/texasholder-poker
              cd /home/ubuntu/texasholder-poker
              
              # Set environment variables
              echo "SPRING_DATASOURCE_URL=jdbc:mysql://${aws_db_instance.texasholder_mysql.endpoint}:3306/texasholder?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&serverTimezone=UTC" >> .env
              echo "SPRING_DATASOURCE_USERNAME=${var.db_username}" >> .env
              echo "SPRING_DATASOURCE_PASSWORD=${var.db_password}" >> .env
              echo "SPRING_PROFILES_ACTIVE=production" >> .env
              
              # Build and start the application
              docker-compose up -d
              EOF
}

# Create EC2 instances for frontend
resource "aws_instance" "frontend_instances" {
  count                  = var.frontend_instance_count
  ami                    = var.ami_id
  instance_type          = var.frontend_instance_type
  subnet_id              = aws_subnet.public_subnets[count.index % length(aws_subnet.public_subnets)].id
  vpc_security_group_ids = [aws_security_group.frontend_sg.id]
  key_name               = var.ssh_key_name
  iam_instance_profile   = aws_iam_instance_profile.texasholder_instance_profile.name

  tags = {
    Name        = "texasholder-frontend-${count.index}"
    Environment = var.environment
    Project     = "texasholder-poker"
    Role        = "frontend"
  }

  user_data = <<-EOF
              #!/bin/bash
              sudo apt-get update -y
              sudo apt-get install -y docker.io docker-compose
              sudo systemctl enable docker
              sudo systemctl start docker
              sudo usermod -aG docker ubuntu
              
              # Clone the repository
              git clone https://github.com/yourusername/texasholder-poker.git /home/ubuntu/texasholder-poker
              cd /home/ubuntu/texasholder-poker
              
              # Set environment variables
              echo "VITE_API_BASE_URL=http://${aws_instance.backend_instances[0].private_ip}:8080" >> vue-client/.env
              
              # Build and start the frontend
              cd vue-client
              docker-compose up -d
              EOF
}

# Create Application Load Balancer for backend
resource "aws_lb" "backend_alb" {
  name               = "texasholder-backend-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.backend_sg.id]
  subnets            = aws_subnet.public_subnets[*].id

  enable_deletion_protection = false

  tags = {
    Name        = "texasholder-backend-alb"
    Environment = var.environment
    Project     = "texasholder-poker"
  }
}

# Create ALB Target Group for backend
resource "aws_lb_target_group" "backend_tg" {
  name     = "texasholder-backend-tg"
  port     = 8080
  protocol = "HTTP"
  vpc_id   = aws_vpc.texasholder_vpc.id

  health_check {
    path                = "/actuator/health"
    port                = 8080
    protocol            = "HTTP"
    interval            = 30
    timeout             = 5
    healthy_threshold   = 3
    unhealthy_threshold = 3
  }

  tags = {
    Name        = "texasholder-backend-tg"
    Environment = var.environment
    Project     = "texasholder-poker"
  }
}

# Create ALB Listener for backend
resource "aws_lb_listener" "backend_listener" {
  load_balancer_arn = aws_lb.backend_alb.arn
  port              = 8080
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.backend_tg.arn
  }
}

# Attach backend instances to target group
resource "aws_lb_target_group_attachment" "backend_attachment" {
  count            = var.backend_instance_count
  target_group_arn = aws_lb_target_group.backend_tg.arn
  target_id        = aws_instance.backend_instances[count.index].id
  port             = 8080
}

# Create Application Load Balancer for frontend
resource "aws_lb" "frontend_alb" {
  name               = "texasholder-frontend-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.frontend_sg.id]
  subnets            = aws_subnet.public_subnets[*].id

  enable_deletion_protection = false

  tags = {
    Name        = "texasholder-frontend-alb"
    Environment = var.environment
    Project     = "texasholder-poker"
  }
}

# Create ALB Target Group for frontend
resource "aws_lb_target_group" "frontend_tg" {
  name     = "texasholder-frontend-tg"
  port     = 80
  protocol = "HTTP"
  vpc_id   = aws_vpc.texasholder_vpc.id

  health_check {
    path                = "/"
    port                = 80
    protocol            = "HTTP"
    interval            = 30
    timeout             = 5
    healthy_threshold   = 3
    unhealthy_threshold = 3
  }

  tags = {
    Name        = "texasholder-frontend-tg"
    Environment = var.environment
    Project     = "texasholder-poker"
  }
}

# Create ALB Listener for frontend (HTTP)
resource "aws_lb_listener" "frontend_listener_http" {
  load_balancer_arn = aws_lb.frontend_alb.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type = "redirect"
    
    redirect {
      port        = "443"
      protocol    = "HTTPS"
      status_code = "HTTP_301"
    }
  }
}

# Create ALB Listener for frontend (HTTPS)
resource "aws_lb_listener" "frontend_listener_https" {
  load_balancer_arn = aws_lb.frontend_alb.arn
  port              = 443
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-2016-08"
  certificate_arn   = var.ssl_certificate_arn

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.frontend_tg.arn
  }
}

# Attach frontend instances to target group
resource "aws_lb_target_group_attachment" "frontend_attachment" {
  count            = var.frontend_instance_count
  target_group_arn = aws_lb_target_group.frontend_tg.arn
  target_id        = aws_instance.frontend_instances[count.index].id
  port             = 80
}

# Output useful information
output "vpc_id" {
  value = aws_vpc.texasholder_vpc.id
}

output "public_subnet_ids" {
  value = aws_subnet.public_subnets[*].id
}

output "private_subnet_ids" {
  value = aws_subnet.private_subnets[*].id
}

output "backend_alb_dns_name" {
  value = aws_lb.backend_alb.dns_name
}

output "frontend_alb_dns_name" {
  value = aws_lb.frontend_alb.dns_name
}

output "database_endpoint" {
  value = aws_db_instance.texasholder_mysql.endpoint
}

output "backend_instance_ips" {
  value = aws_instance.backend_instances[*].public_ip
}

output "frontend_instance_ips" {
  value = aws_instance.frontend_instances[*].public_ip
}
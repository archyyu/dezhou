# Terraform configuration for Dezhou Poker AWS Deployment
# This configuration sets up a complete VPC, security groups, and EC2 instances
# for running the Dezhou Poker application with Docker

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

# Create a VPC for the Dezhou Poker application
resource "aws_vpc" "dezhou_vpc" {
  cidr_block           = var.vpc_cidr_block
  enable_dns_support   = true
  enable_dns_hostnames = true
  instance_tenancy     = "default"

  tags = {
    Name        = "dezhou-poker-vpc"
    Environment = var.environment
    Project     = "dezhou-poker"
  }
}

# Create public subnets in multiple availability zones
resource "aws_subnet" "public_subnets" {
  count                   = length(var.public_subnet_cidrs)
  vpc_id                  = aws_vpc.dezhou_vpc.id
  cidr_block              = var.public_subnet_cidrs[count.index]
  availability_zone       = var.availability_zones[count.index]
  map_public_ip_on_launch = true

  tags = {
    Name        = "dezhou-public-subnet-${count.index}"
    Environment = var.environment
    Project     = "dezhou-poker"
  }
}

# Create private subnets for database
resource "aws_subnet" "private_subnets" {
  count             = length(var.private_subnet_cidrs)
  vpc_id            = aws_vpc.dezhou_vpc.id
  cidr_block        = var.private_subnet_cidrs[count.index]
  availability_zone = var.availability_zones[count.index]

  tags = {
    Name        = "dezhou-private-subnet-${count.index}"
    Environment = var.environment
    Project     = "dezhou-poker"
  }
}

# Create Internet Gateway for public subnets
resource "aws_internet_gateway" "dezhou_igw" {
  vpc_id = aws_vpc.dezhou_vpc.id

  tags = {
    Name        = "dezhou-internet-gateway"
    Environment = var.environment
    Project     = "dezhou-poker"
  }
}

# Create NAT Gateway for private subnets
resource "aws_eip" "nat_eip" {
  domain = "vpc"

  tags = {
    Name        = "dezhou-nat-eip"
    Environment = var.environment
    Project     = "dezhou-poker"
  }
}

resource "aws_nat_gateway" "dezhou_nat" {
  allocation_id = aws_eip.nat_eip.id
  subnet_id     = aws_subnet.public_subnets[0].id

  tags = {
    Name        = "dezhou-nat-gateway"
    Environment = var.environment
    Project     = "dezhou-poker"
  }

  depends_on = [aws_internet_gateway.dezhou_igw]
}

# Create route tables
resource "aws_route_table" "public_route_table" {
  vpc_id = aws_vpc.dezhou_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.dezhou_igw.id
  }

  tags = {
    Name        = "dezhou-public-route-table"
    Environment = var.environment
    Project     = "dezhou-poker"
  }
}

resource "aws_route_table" "private_route_table" {
  vpc_id = aws_vpc.dezhou_vpc.id

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.dezhou_nat.id
  }

  tags = {
    Name        = "dezhou-private-route-table"
    Environment = var.environment
    Project     = "dezhou-poker"
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
  name        = "dezhou-backend-sg"
  description = "Security group for Dezhou Poker backend servers"
  vpc_id      = aws_vpc.dezhou_vpc.id

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
    Name        = "dezhou-backend-sg"
    Environment = var.environment
    Project     = "dezhou-poker"
  }
}

resource "aws_security_group" "frontend_sg" {
  name        = "dezhou-frontend-sg"
  description = "Security group for Dezhou Poker frontend servers"
  vpc_id      = aws_vpc.dezhou_vpc.id

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
    Name        = "dezhou-frontend-sg"
    Environment = var.environment
    Project     = "dezhou-poker"
  }
}

resource "aws_security_group" "database_sg" {
  name        = "dezhou-database-sg"
  description = "Security group for Dezhou Poker database"
  vpc_id      = aws_vpc.dezhou_vpc.id

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
    Name        = "dezhou-database-sg"
    Environment = var.environment
    Project     = "dezhou-poker"
  }
}

# Create RDS MySQL database
resource "aws_db_subnet_group" "dezhou_db_subnet_group" {
  name       = "dezhou-db-subnet-group"
  subnet_ids = aws_subnet.private_subnets[*].id

  tags = {
    Name        = "dezhou-db-subnet-group"
    Environment = var.environment
    Project     = "dezhou-poker"
  }
}

resource "aws_db_instance" "dezhou_mysql" {
  identifier             = "dezhou-mysql"
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
  db_subnet_group_name   = aws_db_subnet_group.dezhou_db_subnet_group.name
  multi_az               = var.db_multi_az

  tags = {
    Name        = "dezhou-mysql"
    Environment = var.environment
    Project     = "dezhou-poker"
  }
}

# Create EC2 instances for backend
resource "aws_instance" "backend_instances" {
  count                  = var.backend_instance_count
  ami                    = var.ami_id
  instance_type          = var.backend_instance_type
  subnet_id              = aws_subnet.public_subnets[count.index % length(aws_subnet.public_subnets)].id
  vpc_security_group_ids = [aws_security_group.backend_sg.id]
  key_name               = var.ssh_key_name

  tags = {
    Name        = "dezhou-backend-${count.index}"
    Environment = var.environment
    Project     = "dezhou-poker"
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
              git clone https://github.com/yourusername/dezhou-poker.git /home/ubuntu/dezhou-poker
              cd /home/ubuntu/dezhou-poker
              
              # Set environment variables
              echo "SPRING_DATASOURCE_URL=jdbc:mysql://${aws_db_instance.dezhou_mysql.endpoint}:3306/dezhou?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&serverTimezone=UTC" >> .env
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

  tags = {
    Name        = "dezhou-frontend-${count.index}"
    Environment = var.environment
    Project     = "dezhou-poker"
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
              git clone https://github.com/yourusername/dezhou-poker.git /home/ubuntu/dezhou-poker
              cd /home/ubuntu/dezhou-poker
              
              # Set environment variables
              echo "VITE_API_BASE_URL=http://${aws_instance.backend_instances[0].private_ip}:8080" >> vue-client/.env
              
              # Build and start the frontend
              cd vue-client
              docker-compose up -d
              EOF
}

# Create Application Load Balancer for backend
resource "aws_lb" "backend_alb" {
  name               = "dezhou-backend-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.backend_sg.id]
  subnets            = aws_subnet.public_subnets[*].id

  enable_deletion_protection = false

  tags = {
    Name        = "dezhou-backend-alb"
    Environment = var.environment
    Project     = "dezhou-poker"
  }
}

# Create ALB Target Group for backend
resource "aws_lb_target_group" "backend_tg" {
  name     = "dezhou-backend-tg"
  port     = 8080
  protocol = "HTTP"
  vpc_id   = aws_vpc.dezhou_vpc.id

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
    Name        = "dezhou-backend-tg"
    Environment = var.environment
    Project     = "dezhou-poker"
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
  name               = "dezhou-frontend-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.frontend_sg.id]
  subnets            = aws_subnet.public_subnets[*].id

  enable_deletion_protection = false

  tags = {
    Name        = "dezhou-frontend-alb"
    Environment = var.environment
    Project     = "dezhou-poker"
  }
}

# Create ALB Target Group for frontend
resource "aws_lb_target_group" "frontend_tg" {
  name     = "dezhou-frontend-tg"
  port     = 80
  protocol = "HTTP"
  vpc_id   = aws_vpc.dezhou_vpc.id

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
    Name        = "dezhou-frontend-tg"
    Environment = var.environment
    Project     = "dezhou-poker"
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
  value = aws_vpc.dezhou_vpc.id
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
  value = aws_db_instance.dezhou_mysql.endpoint
}

output "backend_instance_ips" {
  value = aws_instance.backend_instances[*].public_ip
}

output "frontend_instance_ips" {
  value = aws_instance.frontend_instances[*].public_ip
}
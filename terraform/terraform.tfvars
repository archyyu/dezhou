# Terraform Variables File for Dezhou Poker
# This file contains example values - customize for your deployment

# AWS Configuration
aws_region = "us-east-1"
environment = "production"

# VPC Configuration
vpc_cidr_block = "10.0.0.0/16"
public_subnet_cidrs = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
private_subnet_cidrs = ["10.0.10.0/24", "10.0.11.0/24", "10.0.12.0/24"]
availability_zones = ["us-east-1a", "us-east-1b", "us-east-1c"]

# EC2 Configuration
ami_id = "ami-0c55b159cbfafe1f0" # Ubuntu 22.04 LTS in us-east-1
ssh_key_name = "dezhou-key-pair"
backend_instance_type = "t3.medium"
backend_instance_count = 2
frontend_instance_type = "t3.medium"
frontend_instance_count = 2

# Database Configuration
db_instance_class = "db.t3.medium"
db_allocated_storage = 20
db_name = "dezhou"
db_username = "dezhou_user"
db_password = "aida87014999"
db_multi_az = true

# SSL Configuration
# ssl_certificate_arn = "arn:aws:acm:us-east-1:123456789012:certificate/xxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"

# Tagging
tags = {
  Project     = "dezhou-poker"
  Environment = "production"
  ManagedBy   = "terraform"
  Owner       = "your-team"
}
# Terraform Variables for TexasHolder Poker AWS Deployment

# AWS Configuration
variable "aws_region" {
  description = "AWS region to deploy resources"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Deployment environment (dev, staging, production)"
  type        = string
  default     = "production"
}

# VPC Configuration
variable "vpc_cidr_block" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "CIDR blocks for public subnets"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
}

variable "private_subnet_cidrs" {
  description = "CIDR blocks for private subnets"
  type        = list(string)
  default     = ["10.0.10.0/24", "10.0.11.0/24", "10.0.12.0/24"]
}

variable "availability_zones" {
  description = "Availability zones to use"
  type        = list(string)
  default     = ["us-east-1a", "us-east-1b", "us-east-1c"]
}

# EC2 Configuration
variable "ami_id" {
  description = "AMI ID for EC2 instances"
  type        = string
  default     = "ami-0c55b159cbfafe1f0" # Ubuntu 22.04 LTS
}

variable "ssh_key_name" {
  description = "Name of the SSH key pair"
  type        = string
  default     = "texasholder-key-pair"
}

variable "backend_instance_type" {
  description = "EC2 instance type for backend servers"
  type        = string
  default     = "t3.medium"
}

variable "backend_instance_count" {
  description = "Number of backend instances"
  type        = number
  default     = 2
}

variable "frontend_instance_type" {
  description = "EC2 instance type for frontend servers"
  type        = string
  default     = "t3.medium"
}

variable "frontend_instance_count" {
  description = "Number of frontend instances"
  type        = number
  default     = 2
}

# Database Configuration
variable "db_instance_class" {
  description = "RDS instance class"
  type        = string
  default     = "db.t3.medium"
}

variable "db_allocated_storage" {
  description = "Allocated storage for RDS in GB"
  type        = number
  default     = 20
}

variable "db_name" {
  description = "Database name"
  type        = string
  default     = "texasholder"
}

variable "db_username" {
  description = "Database username"
  type        = string
  default     = "texasholder_user"
  sensitive   = true
}

variable "db_password" {
  description = "Database password"
  type        = string
  default     = "aida87014999"
  sensitive   = true
}

variable "db_multi_az" {
  description = "Enable Multi-AZ deployment for RDS"
  type        = bool
  default     = true
}

# SSL Configuration
variable "ssl_certificate_arn" {
  description = "ARN of the SSL certificate for HTTPS"
  type        = string
  default     = "" # Replace with your actual SSL certificate ARN
}

# Tagging
variable "tags" {
  description = "Additional tags for resources"
  type        = map(string)
  default     = {
    Project     = "texasholder-poker"
    Environment = "production"
    ManagedBy   = "terraform"
  }
}
# TexasHolder Poker - AWS Terraform Deployment

This directory contains Terraform configuration for deploying the TexasHolder Poker application on AWS. The infrastructure includes VPC, subnets, security groups, EC2 instances, RDS database, and Application Load Balancers.

## 🚀 Quick Start

### Prerequisites
- [Terraform](https://www.terraform.io/downloads.html) (v1.0.0+)
- [AWS CLI](https://aws.amazon.com/cli/) configured with credentials
- AWS account with appropriate permissions
- SSH key pair created in AWS (or create one)

### 1. Initialize Terraform

```bash
cd terraform
terraform init
```

### 2. Review the Plan

```bash
terraform plan
```

### 3. Apply the Configuration

```bash
terraform apply
```

### 4. Access Your Deployment

After deployment completes, Terraform will output:
- **Frontend URL**: `http://<frontend-alb-dns-name>` (HTTP redirects to HTTPS)
- **Backend API**: `http://<backend-alb-dns-name>:8080`
- **Database Endpoint**: For internal use

## 🏗️ Infrastructure Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        AWS VPC (10.0.0.0/16)                              │
├─────────────────────────┬─────────────────────────┬─────────────────────────┤
│  Public Subnets          │  Private Subnets        │  Security Groups        │
│  (10.0.1.0/24, etc.)     │  (10.0.10.0/24, etc.)  │                         │
├─────────────────────────┼─────────────────────────┼─────────────────────────┤
│  • Frontend ALB         │  • RDS MySQL            │  • Frontend SG          │
│  • Backend ALB          │  • NAT Gateway          │  • Backend SG           │
│  • Frontend EC2         │                         │  • Database SG          │
│  • Backend EC2          │                         │                         │
└─────────────────────────┴─────────────────────────┴─────────────────────────┘
```

## 📋 Deployment Components

### 1. Networking
- **VPC**: Isolated virtual network (10.0.0.0/16)
- **Public Subnets**: 3 subnets across availability zones for frontend/backend
- **Private Subnets**: 3 subnets across availability zones for database
- **Internet Gateway**: Allows public subnets to access internet
- **NAT Gateway**: Allows private subnets to access internet for updates
- **Route Tables**: Proper routing for public and private traffic

### 2. Security
- **Security Groups**:
  - Frontend: Allows HTTP/HTTPS/SSH
  - Backend: Allows API/WebSocket/SSH
  - Database: Only allows MySQL from backend
- **Network ACLs**: Default allow rules (customize as needed)

### 3. Compute
- **Frontend Instances**: 2x t3.medium EC2 instances running Docker
- **Backend Instances**: 2x t3.medium EC2 instances running Docker
- **Auto Scaling**: Ready (can be added)
- **User Data**: Automatically installs Docker and deploys the application

### 4. Database
- **RDS MySQL 8.0**: Multi-AZ deployment for high availability
- **Storage**: 20GB GP2 SSD
- **Backup**: Automatic backups enabled
- **Security**: Private subnets only, no public access

### 5. Load Balancing
- **Frontend ALB**: HTTP → HTTPS redirect, routes to frontend instances
- **Backend ALB**: Routes API traffic to backend instances
- **Health Checks**: Monitor instance health
- **Target Groups**: Distribute traffic across instances

### 6. DNS & SSL
- **Route 53**: Ready for DNS configuration
- **ACM**: SSL certificate integration (add your ARN)

## 🔧 Configuration

### Variables

Edit `terraform.tfvars` to customize your deployment:

```hcl
# AWS Region
aws_region = "us-east-1"

# Instance Sizing
backend_instance_type = "t3.medium"
frontend_instance_type = "t3.medium"

# Scaling
backend_instance_count = 2
frontend_instance_count = 2

# Database
db_instance_class = "db.t3.medium"
db_allocated_storage = 20
db_multi_az = true
```

### Customization

1. **SSH Key**: Update `ssh_key_name` with your AWS key pair name
2. **SSL Certificate**: Uncomment and add your ACM certificate ARN
3. **AMI**: Verify the AMI ID for your region
4. **Database Credentials**: Update username/password

## 📊 Cost Estimation

### Monthly Costs (Approximate)

| Resource | Type | Count | Monthly Cost |
|----------|------|-------|--------------|
| VPC | - | 1 | Free |
| Public Subnets | - | 3 | Free |
| Private Subnets | - | 3 | Free |
| Internet Gateway | - | 1 | Free |
| NAT Gateway | - | 1 | ~$36 |
| Frontend EC2 | t3.medium | 2 | ~$60 |
| Backend EC2 | t3.medium | 2 | ~$60 |
| RDS MySQL | db.t3.medium | 1 | ~$50 |
| ALB | - | 2 | ~$20 |
| **Total** | | | **~$226/month** |

*Note: Costs vary by region and usage. Use AWS Pricing Calculator for accurate estimates.*

## 🛠️ Deployment Workflow

### 1. Initialize
```bash
terraform init
```

### 2. Plan
```bash
terraform plan -out=texasholder.plan
```

### 3. Apply
```bash
terraform apply texasholder.plan
```

### 4. Verify
```bash
# Check outputs
terraform output

# SSH to instances
ssh -i ~/.ssh/texasholder-key-pair.pem ubuntu@<instance-ip>
```

### 5. Destroy (when needed)
```bash
terraform destroy
```

## 🔒 Security Best Practices

### 1. Database Security
- **Private Subnets**: Database is not publicly accessible
- **Security Groups**: Only backend instances can access MySQL
- **Multi-AZ**: Automatic failover for high availability

### 2. Instance Security
- **SSH Access**: Restrict to specific IPs in production
- **Security Groups**: Minimal required ports open
- **IAM Roles**: Use IAM roles instead of access keys

### 3. Network Security
- **VPC Flow Logs**: Enable for monitoring
- **Network ACLs**: Add additional layer of security
- **WAF**: Consider adding Web Application Firewall

## 📈 Scaling Options

### 1. Horizontal Scaling
```hcl
# Increase instance count
backend_instance_count = 4
frontend_instance_count = 4
```

### 2. Vertical Scaling
```hcl
# Upgrade instance types
backend_instance_type = "t3.large"
frontend_instance_type = "t3.large"
db_instance_class = "db.t3.large"
```

### 3. Auto Scaling
Add auto scaling configuration:
```hcl
resource "aws_autoscaling_group" "backend_asg" {
  # Auto scaling configuration
}
```

## 🧪 Testing the Deployment

### 1. Health Checks
```bash
# Check frontend health
curl http://<frontend-alb-dns-name>

# Check backend health
curl http://<backend-alb-dns-name>:8080/actuator/health

# Check database connection
mysql -h <database-endpoint> -u texasholder_user -p
```

### 2. Load Testing
```bash
# Use your existing load testing scripts
k6 run --vus 50 --duration 30s load_test.js
```

## 📚 Troubleshooting

### Common Issues

1. **SSH Connection Failed**:
   - Verify security group allows SSH
   - Check key pair name
   - Ensure instance is running

2. **Database Connection Failed**:
   - Verify RDS endpoint
   - Check security group rules
   - Validate credentials

3. **ALB Health Check Failed**:
   - Verify health check paths
   - Check instance security groups
   - Ensure application is running

### Debugging Commands
```bash
# View Terraform state
terraform state list

# View specific resource
terraform state show aws_instance.backend_instances

# Import existing resources
terraform import aws_vpc.texasholder_vpc vpc-12345678
```

## 📖 Additional Resources

- [Terraform AWS Provider Documentation](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
- [AWS VPC Best Practices](https://docs.aws.amazon.com/vpc/latest/userguide/vpc-best-practices.html)
- [AWS RDS Best Practices](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_BestPractices.html)
- [Terraform Best Practices](https://www.terraform-best-practices.com/)

## 🤝 Contributing

If you make improvements to the Terraform configuration:

1. **Update Documentation**: Keep README in sync
2. **Test Changes**: Verify in non-production environment
3. **Add Comments**: Explain complex configurations
4. **Follow Best Practices**: Use Terraform modules where appropriate

## 🎯 Next Steps

1. **Add Monitoring**: CloudWatch alarms and dashboards
2. **Implement CI/CD**: Automate deployments with GitHub Actions
3. **Add Backup**: Regular database backups
4. **Implement Logging**: Centralized logging with CloudWatch Logs
5. **Add Auto Scaling**: Automatic scaling based on load

---

**Happy Deploying!** 🚀☁️
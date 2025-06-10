☸️ Kubernetes Deployment with Helm
```bash
cd..
# Ensure you have Kind and Helm installed
docker build -t checklist-app:latest .
kind load docker-image checklist-app:latest

helm install checklist ./chart --set image.tag=latest --wait

```
To test the application in Kubernetes using a local KiND cluster:

```shell
# Create cluster
kind create cluster

# Build application image (do that at the root of the repository)
docker build . -t tmp:cookbook

# Load image into KinD node
kind load docker-image tmp:cookbook

# Deploy application (do that in this directory)
kubectl apply -f.

# Wait for application and ollama to be up and running
# (Note: with big models and/or slow internet connection,
# you might need a longer timeout!)
kubectl wait deployment cookbook ollama --for=condition=Available --timeout=5m

# Forward local port to our Spring application
kubectl port-forward service/cookbook 1234:80

# Send a test request
curl localhost:1234/recipe -d "instructions=gimme chocolate dessert plz"

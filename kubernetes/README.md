### Kubernetes resources

#### Setup

```
kubectl create namespace <NAME_SPACE>
```

```
kubectl create -n <NAME_SPACE> secret generic discord-deputy \
    --from-literal=rabbit_user='<RABBIT_USER>' \
    --from-literal=rabbit_pass='<RABBIT_PASS>' \
    --from-literal=mongo_user='<MONGO_USER>' \
    --from-literal=mongo_pass='<MONGO_PASS>' \
    --from-literal=discord_token='<DISCORD_TOKEN>'
```

```
kubectl apply -k overlay/<ENV>
```

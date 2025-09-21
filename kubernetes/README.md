### Kubernetes resources

#### Setup

```
kubectl create namespace <NAME_SPACE>
```

```
kubectl create -n <NAME_SPACE> secret generic discord-deputy \
    --from-literal=discord_token='<DISCORD_TOKEN>'
```

```
kubectl apply -k overlay/<ENV>
```

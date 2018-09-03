in minikube

- get INGRESS_PORT using:
    export INGRESS_PORT=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}')

- get INGRESS_HOST using:
  export INGRESS_HOST=$(minikube ip)

- and curl the service via the ingress gateway using:

curl \
     -w '\n' \
     -H 'Host: k8sapp.myns.svc.cluster.local' \
     http://$INGRESS_HOST:$INGRESS_PORT
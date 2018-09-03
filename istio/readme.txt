in minikube

- to access the service you need to get the INGRESS_IP and INGRESS_PORT using:
    export INGRESS_IP_ADDRESS_PORT=$(kubectl get node  -o 'jsonpath={.items[0].status.addresses[0].address}'):$(kubectl get svc knative-ingressgateway -n istio-system   -o 'jsonpath={.spec.ports[?(@.port==80)].nodePort}')

- to get the service's host, use:
   export HOST_URL=$(kubectl get services.serving.knative.dev k8sapp -o jsonpath='{.status.domain}')

- and curl the service via the ingress gateway using:

curl \
     -w '\n' \
     -H 'Host: ${HOST}' \
     http://$INGRESS_IP_ADDRESS_PORT
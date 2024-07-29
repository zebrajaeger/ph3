## Erstellen eines selbstsignierten Zertifikats mit OpenSSL
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365 -nodes

 
## Erstellen des Keystores im PKCS12-Format
openssl pkcs12 -export -in cert.pem -inkey key.pem -out keystore.p12 -name selfsigned -password pass:changeit

## Copy

cp ./keystore.p12 ../server-java/src/main/resources
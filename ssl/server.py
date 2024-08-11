# -*- coding: utf-8 -*-

import os
import sys
import subprocess

def run_command(command):
    print(f"$ {command}", end=" -> ")

    process = subprocess.Popen(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    stdout, stderr = process.communicate()
    if process.returncode != 0:
        print(f"Error: {stderr.decode('utf-8')}")
        sys.exit(1)
    else:
        print("ok")
        # print(stdout.decode('utf-8'))


def generateConfig(s,cfg):
    return f"""
[ req ]
default_bits        = 2048
default_keyfile     = {s['name']}.key
distinguished_name  = req_distinguished_name
req_extensions      = v3_req

[ req_distinguished_name ]
countryName            = Country Name (2 letter code)
countryName_default    = {cfg['defaults']['country']}
stateOrProvinceName    = State or Province Name (full name)
stateOrProvinceName_default = {cfg['defaults']['stateOrProvince']}
localityName           = Locality Name (eg, city)
localityName_default   = {cfg['defaults']['locality']}
organizationName       = Organization Name (eg, company)
organizationName_default = {cfg['defaults']['organization']}
commonName             = Common Name (eg, your name or your server's hostname)
commonName_default     = {s['name']}

[ v3_req ]
subjectAltName = @alt_names
keyUsage = critical, digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth

[ alt_names ]
IP.1 = {s['ip']}
DNS.1 = {s['name']}
DNS.2 = {s['name']}.ph5
DNS.3 = {s['name']}.local
    """

def generate(s, cfg):

    directory = s['name']
    if not os.path.exists(directory):
        os.makedirs(directory)

    host = s['name']
    cfgName = f"{host}.cfg"

    with open(f"{cfgName}", "w") as caCfg:
        caCfg.write(generateConfig(s,cfg))


    caDirectory = "ca"
    caPassword = cfg['ca']['keyPassword']

    days = cfg['defaults']['days']
    keyPassword = cfg['defaults']['keyPassword']
    storePassword = cfg['defaults']['storePassword']

    # https: openssl s_client -connect 192.168.178.31:8443
    # https: openssl s_client -connect 192.168.178.31:8443 -servername 192.168.178.31 | openssl x509 -noout -text | grep -A 1 "Subject Alternative Name"
    # CSR: openssl req -in pcw/pcw.csr -text -noout | grep DNS
    # CRT: openssl x509 -noout -text -in pcw/pcw.crt | grep DNS
    # Android: openssl pkcs12 -info --legacy -in pcw/pcw-android.p12 -nodes -passin pass:{storePassword}
    # Java: openssl pkcs12 -info  -in pcw/pcw-java.p12 -nodes -passin pass:{storePassword}
    commands = [
        # Generate Key
        f"openssl genpkey -algorithm RSA -out {directory}/{host}.key -aes256 -pass pass:{keyPassword}",

        # Create Certificate Request
        f"openssl req -new -config {cfgName} -key {directory}/{host}.key -out {directory}/{host}.csr -passin pass:{keyPassword} -batch",

        # Sign key with Root Certificate
        f"openssl x509 -req -extfile {cfgName} -in {directory}/{host}.csr -CA {caDirectory}/ca.crt -CAkey {caDirectory}/ca.key -CAcreateserial -out {directory}/{host}.crt -days {days} -extensions v3_req -passin pass:{caPassword}",

        # Put Server Certificate and key in legacy-keystore for Android
        f"openssl pkcs12 -export --legacy -out {directory}/{host}-android.p12 -inkey {directory}/{host}.key -in {directory}/{host}.crt -certfile {caDirectory}/ca.crt -passout pass:{storePassword} -passin pass:{keyPassword} -name \"{host}\"",
        #f"openssl pkcs12 -info --legacy -in {directory}/{host}-android.p12 -nodes -passin pass:{storePassword}" ,

        # Put Server Certificate and key in (non-legacy) keystore for Java
        f"openssl pkcs12 -export          -out {directory}/{host}-java.p12    -inkey {directory}/{host}.key -in {directory}/{host}.crt -certfile {caDirectory}/ca.crt -passout pass:{storePassword} -passin pass:{keyPassword} -name \"{host}\"",
        #f"openssl pkcs12 -info          -in {directory}/{host}-java.p12    -nodes -passin pass:{storePassword}"
    ]

    for command in commands:
        run_command(command)

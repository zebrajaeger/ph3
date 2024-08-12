# -*- coding: utf-8 -*-

import os
import sys
import subprocess
import json

def run_command(command):
    print(f"$ {command}", end=" -> ")

    process = subprocess.Popen(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    stdout, stderr = process.communicate()
    if process.returncode != 0:
        print(f"Error: {stderr.decode('utf-8')}")
        sys.exit(1)
    else:
        print("ok")

def generateConfig(cfg):
    return f"""
[ ca ]
default_ca = CA_default

[ CA_default ]
dir               = ./ca
certificate       = $dir/ca.crt
private_key       = $dir/ca.key
new_certs_dir     = $dir/newcerts
database          = $dir/index.txt
serial            = $dir/serial
default_md        = sha256
default_days      = 3650
policy            = policy_strict

[ policy_strict ]
commonName        = supplied
countryName       = match
stateOrProvinceName = match
organizationName  = match

[ req ]
default_bits      = 4096
default_keyfile   = ca.key
distinguished_name = req_distinguished_name
x509_extensions   = v3_ca

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
commonName_default     = {cfg['ca']['common']}

[ v3_ca ]
subjectKeyIdentifier = hash
authorityKeyIdentifier = keyid:always,issuer
basicConstraints = critical,CA:TRUE,pathlen:0
keyUsage = critical, digitalSignature, cRLSign, keyCertSign
    """

def generate(cfg):

    directory = "ca"
    if not os.path.exists(directory):
        os.makedirs(directory)
        
    with open('ca.cfg', "w") as caCfg:
        caCfg.write(generateConfig(cfg))

    caPassword = cfg['ca']['keyPassword']
    days = cfg['defaults']['days']

    commands = [
        # Generate key
        f"openssl genpkey -algorithm RSA -out \"{directory}/ca.key\" -aes256 -pass \"pass:{caPassword}\"",

        # Self sign
        f"openssl req -config ca.cfg -new -x509 -days {days} -key \"{directory}/ca.key\" -out \"{directory}/ca.crt\" -extensions v3_ca -passin \"pass:{caPassword}\" -batch"
    ]

    for command in commands:
        run_command(command)

    # os.remove('ca.cfg.temp')

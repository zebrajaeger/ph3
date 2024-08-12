# Certificate

Maybe  change inventory.json to your specific requirements.

## Create keys and certificates

    $> generate.cmd

or

    $> python all.py

## Copy Java keystore into server resources

    $> cp <myServer>/<myServer>-java.p12 ../server-java/src/main/resources

## npm run start:https (Angular serve)

I takes the ssl stuff from ssl/pcw directory:
- &lt;project root&gt;/ssl/pcw/pcw.key.nopass
- &lt;project root&gt;/ssl/pcw/pcw.crt

Please note: the key can't have a password protection because Angular doesn't support it.    
The .nopass file is generated with an "nopass":true entry in the inventory.json/server structure.    

## Import CA Root Certificate into Android System

- copy ca/ca.crt to device
- Go to Settings ->  Security -> Encryption -> Install & credentials ->  Install a certificate -> CA certificate
- Click 'install anyway'
- choose 'ca.crt'
- It should say 'CA certificate installed'
- Open chrome and open the server Url (like <https://pcw:8443>)
  - The page should be opened withouut any warning

## Import into Windows

- start certmgr.msc
- "Vertrauenswürdige Stammzertifizierungsstellen" > "Zertifikate"
- Klicke mit der rechten Maustaste und wähle "Alle Aufgaben" > "Importieren".
- Folge dem Assistenten und wähle die Datei ca/ca.crt aus.
- Restart Browser

## Import into Linux (not tested))

- Kopiere ca/ca.crt das Zertifikat nach /usr/local/share/ca-certificates/:
- sudo cp server.crt /usr/local/share/ca-certificates/
- sudo update-ca-certificates
- Restart Browser

# Selenium Grid Extensions

[![Build Status](https://travis-ci.org/sterodium/selenium-grid-extensions.svg?branch=master)](https://travis-ci.org/sterodium/selenium-grid-extensions)
[![Coverage Status](https://coveralls.io/repos/sterodium/selenium-grid-extensions/badge.svg?branch=master&service=github)](https://coveralls.io/github/sterodium/selenium-grid-extensions?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/io.sterodium/selenium-grid-extensions.svg)](https://maven-badges.herokuapp.com/maven-central/io.sterodium/selenium-grid-extensions)
[![][license img]][license]
[![Gitter Chat](http://img.shields.io/badge/chat-online-brightgreen.svg)](https://gitter.im/sterodium/selenium-grid-extensions)


Set of Selenium Grid extensions for a better UI tests.

## Full Documentation

See the [Wiki](https://github.com/sterodium/selenium-grid-extensions/wiki) for documentation, examples and other information.

## Communication

- Twitter: [@Sterodium](http://twitter.com/Sterodium)
- [GitHub Issues](https://github.com/sterodium/selenium-grid-extensions/issues)
- [Gitter Chat](https://gitter.im/sterodium/selenium-grid-extensions)

## What does this project do?

#### 1) Allows you to run Sikuli tests remotely on the grid

Remove pain of testing complex UI components by hacking DOM. With this extension you are able to combine Selenium tests
with [Sikuli](http://www.sikuli.org/) image recognition and run them on the grid.

Even more! Sikuli allows you to automate anything you see.

#### 2) Download file from Selenium grid nodes

Downloading files in Selenium tests? Get them to your machine and check contents. Now it's easy.

#### 3) Upload resources on Selenium grid nodes

You might have some file upload tests. Uploading files to remote environment is not a problem anymore.

**_More to come..._**

## Getting started

Extensions need to be installed on Selenium Grid.
It is obligatory to have Extension proxy installed in the hub.

### Binaries

Binaries and dependency information for Maven, Gradle and other build tools can be found at [http://search.maven.org](http://search.maven.org/#search%7Cga%7C1%7Cio.sterodium).

#### On client side for Maven:

```xml
<dependency>
    <groupId>io.sterodium</groupId>
    <artifactId>sikuli-extension-client</artifactId>
    <version>x.y.z</version>
</dependency>
<dependency>
    <groupId>io.sterodium</groupId>
    <artifactId>file-extension-client</artifactId>
    <version>x.y.z</version>
</dependency>
```

#### On grid side:

**Selenium hub:**

- Get [Hub extensions proxy](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22io.sterodium%22%20AND%20a%3A%22extension-proxy%22) jar file and put it together with **selenium-server-standalone-x.y.z.jar**
- Modify hubConfig.json (servlets and capability matcher property) 
```json
...
 "servlets": [
    "io.sterodium.extensions.hub.proxy.HubRequestsProxyingServlet"
  ],
...
  "capabilityMatcher": "io.sterodium.extensions.capability.CustomCapabilityMatcher"
...
```
Launch grid with 
```
java -cp "selenium-server-standalone-2.48.2.jar:extension-proxy-x.y.z.jar" org.openqa.grid.selenium.GridLauncher -role hub -hubConfig hubConfig.json
```

**Selenium node**:
- Get [All node extensions](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.sterodium%22%20AND%20a%3A%22all-node-extensions%22) jar file and put it together with with **selenium-server-standalone-x.y.z.jar**
- Modify **nodeConfig.json** (capabilities and servlet properties) 

```json
...
"capabilities": [
    {
      "extension.sikuliCapability": true
    }
  ],
...
"servlets": "io.sterodium.extensions.node.SikuliExtensionServlet,io.sterodium.extensions.node.upload.FileUploadServlet,io.sterodium.extensions.node.download.FileDownloadServlet"
...
```
Launch node with 
```
java -cp "selenium-server-standalone-2.48.2.jar:all-node-extensions-x.y.z.jar" org.openqa.grid.selenium.GridLauncher -role node -nodeConfig nodeConfig.json
```


### Hello world

#### Sikuli extension

Get session id from RemoteWebDriver

```java
String sessionId = remoteWebDriver.getSessionId();
```

Create Sikuli client and upload images to selenium node:

```java
SikuliExtensionClient client = new SikuliExtensionClient(host, port, sessionId);
client.uploadResourceBundle("my_images_folder");
```

Locate image on screen and click:

```java
TargetFactory targetFactory = client.getTargetFactory();
ImageTarget imageTarget = targetFactory.createImageTarget("image.png");

DesktopScreenRegion desktop = client.getDesktop();
ScreenRegion screenRegion = desktop.find(imageTarget);

Mouse mouse = client.getMouse();
mouse.click(screenRegion.getCenter());
```

Refer to [Sikuli docs](https://code.google.com/p/sikuli-api/wiki/BasicUsage?hl=en) for basic Sikuli API usage.

#### File extension

File upload to selenium node:

```java
FileExtensionClient fileExtensionClient = new FileExtensionClient(host, port, sessionId);
String uploadPath = fileExtensionClient.upload(resourceBundlePath);
```

File download from selenium node:

```java
FileExtensionClient fileExtensionClient = new FileExtensionClient(host, port, sessionId);
File fileFromNode = fileExtensionClient.download(pathToFile);
```

## Build

```
$ git clone git@github.com:sterodium/selenium-grid-extensions.git
$ cd selenium-grid-extensions/
$ mvn install
```

[license]:LICENSE
[license img]:https://img.shields.io/badge/License-Apache%202-blue.svg

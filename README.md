# BoomiRouter

BoomiRouter is a library which allows you to do routing in Dell Boomi using API and Web Service Server Components.

## Getting Started

Please download the [jar](jar/boomirouter-0.3.4.jar?raw=true) from github.


### Create a Custom Library

![Alt text](resources/Router_CustomLibrary.png?raw=true "BoomiRouter")

### Create a Process

Create a Process starting a Web Services Server

![Alt text](resources/Router_Process.png?raw=true "BoomiRouter")

The WSS Operation will have a Single Data as Input and None as Output:

![Alt text](resources/Router_WSS.png?raw=true "BoomiRouter")

Add a Data Process Shape with Groovy:

```
import java.util.Properties;
import java.io.InputStream;
import com.boomi.proserv.b2b.router.BoomiRouter;

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);
    
    BoomiRouter router = new BoomiRouter()
    router.handle(dataContext);

    //dataContext.storeStream(is, props);
}

```

If you have API Management, create an API

![Alt text](resources/Router_API_0.png?raw=true "BoomiRouter")

![Alt text](resources/Router_API_1.png?raw=true "BoomiRouter")

### Update of boomirouter.properties

Update boomirouter.properties as following

```
com.boomi.proserv.b2b.router.BoomiRouter.ID.elementNumber=1
com.boomi.proserv.b2b.router.BoomiRouter.ID.xpath=/DeliveryHeader/messageReceiverIdentification/PartnerIdentification/GlobalBusinessIdentifier

com.boomi.proserv.b2b.router.BoomiRouter.mapping.<TARGET_ID>.target_host=localhost:9090
com.boomi.proserv.b2b.router.BoomiRouter.mapping.<TARGET_ID>.operation=<OPERATION>
com.boomi.proserv.b2b.router.BoomiRouter.mapping.<TARGET_ID>.user=<USER>
com.boomi.proserv.b2b.router.BoomiRouter.mapping.<TARGET_ID>.password=<PASSWORD>
```

The First line configure the index of the Mime element to read when applying the XPath function (here 1, so the second part) to get the ID of the target
The Second line is the XPath to apply to get the value of the ID itself using the TARGET_ID

The following lines are defined for each target. You should have four lines for each target.

Place the file in *ATOM_DIR/conf* folder

#### Testing

To call the Router using **/ws/rest/router/Routing/** or **/ws/simple/createRouting**
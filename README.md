# WiFi_Permission_Example
 This is a simple Android project designed to demonstrate how to request permissions following the Android recommended workflow. 
 
 We choose as the test case for this project, to get the SSID of the connected WiFi interface. 

To do so, some conditions must be met by the application. The permission ACCESS_FINE_LOCATION must be granted by the user because it has dangerous protection level, and the permissions ACCESS_WIFI_STATE and ACCESS_NETWORK_STATE must be declared on the Manifest because they have normal protection level.

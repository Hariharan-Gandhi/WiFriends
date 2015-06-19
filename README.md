WiFriends
=========
> ### Project: CN-Lab PS TU Darmstadt (WiSe-2014/15)

Description:
-----------
**One-Liner:**  Decentralized Social Networking App using WiFi Direct 
(An Android App which helps in Socializing with Trusted Friends in
Proximity using D2D Communication)

As a part of the research idea, from the [Peer-to-Peer Systems Engineering Lab, TU Darmstadt](http://www.ps.tu-darmstadt.de/teaching/cnlab/), to estimate the feasibility of latest mobile technology in creating an **ad-hoc peer to peer networks**, this project involved developing a Decentralized Social Networking application, that uses **Wi-Fi P2P API** for the overlay creation and it dynamically discovers peers in proximity to exchange profile information in `JSON` format.

The project considers **security** as a major aspect of concern in such ad-hoc communication and addresses by data encryption using pre-shared key `(AES 256)` exchanged securely via `Near Field Communication(NFC)` during the add friend stage. _Material Design UI_ (Android Lollipop) is also incorporated into the application.

**Technologies Tag:** `Android`, `Java`, `SQLite`, `WifiP2P API`, `Cryptography`, `AES 256 Encryption`, `Spongy Castle API`, `Git`, `Material Design UI`, `Android Lollipop`, `Android Studio`


Key features: 
------------
* _Decentralized and without Internet_ - using Wi-Fi P2p API of Android
* Exchanges only with _Trusted Friends_ - using NFC and local PINs to securely Add Friends to Trust Zone
* Collected profiles are _stored Locally_ on user's devices - using `SQLite` DataHandlers and `JSON` objects
*  User can later browse friends Profile - using Profile View UIs
*  Automatic Content Exchange whenever friends are in Proximity via any D2D Communication Technique(WifiP2P) Network Service Discovery 
*  The app should be Secure - Only friends can exchange Contents  - using Encrypted exchange of Profile data AES-256 
*  UI - As Less Interactions as possible - Implementing as foreground Service
*  UI – Appealing to view profiles

Code Modules:
--------
* **Module:** `MAIN`  
* **Module:** `addfriends`
* **Module:** `castle`
* **Module:** `datahandlers`
* **Module:** `profilepage`

####Tutor:
Leonhard Nobach, M.Sc.

#### Members:
+ Hariharan Gandhi (hariharangandhi17@gmail.com)
+ Harini Gunabalan (harinigunabalan24@gmail.com)

Screenshots:
--------

**WiFriends App's Material UI**
* The Material Design UI and the Add Friends Feature screenshot is shown:
![Alt text](https://cloud.githubusercontent.com/assets/9555615/8251643/9433a346-167e-11e5-9eb7-cf5fe89ebfa1.jpg "Test")

**News Feed Before Profile Exchange**
* The News Feed with the friend's current Status is shown on two friend devices. 
![Alt text](https://cloud.githubusercontent.com/assets/9555615/8251639/8abb7c80-167e-11e5-987b-f57a2d4b9f68.jpg "Before Exchange")

**Users Status Updated**
* The user's update their status on their respective devices.
![Alt text](https://cloud.githubusercontent.com/assets/9555615/8251640/8cca88e0-167e-11e5-9459-bb712392aa9f.jpg "Before Exchange")

**News Feed After Profile Exchange**
* Now the devices come into proximity, connect to each other through WiFi Direct to exchange profiles and the updated statuses are reflected on the friend's devices.
![Alt text](https://cloud.githubusercontent.com/assets/9555615/8251641/8f7d8d26-167e-11e5-8552-3b04ff38d43b.jpg "Before Exchange")

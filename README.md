# ConneKt - Unofficial (yet better) Sophos Client App

This project focusses on providing a better client side interface for Sophos Client Side Wifi Login Page in VITAP University

This app is not intended to be uploaded in playstore as the target audience is not huge enough.<br>
No worries , the app is equipped with an auto update feature and you can update by clicking the update button
in the main page(if any updates found)

(**Not sure if it would work in other Sophos Backed Netwoks**)

# Extra Features
  (Apart from conventional login and logout)
  * **Smart Logout** : <br><br>
    The feature will Logout your wifi credential when the Wifi signal strength drops below a minimum threshold. 
    When the wifi signal strenght drops below a set min threshold, the app automatically logout your wifi credentails to
    prevent your account from getting locked there
  
  * **Timer Logout** :<br><br>
    Just like alarm , your wifi credentials will be logged out automatically when it reaches the time you set

# FAQs
  * **Do you collect all the login credentials used to login in this app ?** <br><br>
    No , not at all. This app is being developed as a part of my learning curve in app developement
    and will continue to be. No intensions to collect any data from it in any way

  * **How can I beleive your claims that you dont collect any data ?** <br><br>
    Follow the procedures from this [StackOverflow page](https://stackoverflow.com/questions/32920919/how-to-monitor-http-get-post-etc-requests-that-my-app-is-making-in-android)
    with that you can monitor the web requests made by this app, if you find anything fishy shoot me up, we can talk !
    
  * **How can I report bugs?** <br><br>
    Create a new issue in this repository , with a screenshot of the issue you are facing  
    along with proper guidance on how to reproduce the issue. I'll try to fix it!

# Contribution Guidance
  * **As Feature Recomendations:**<br><br>
    One can submit a feature request in the issues section of this repository , I will process it further
    
  * **As Software :**  <br><br>
    One can create a pull request of this project , and send a merge request
    
  * **As Coffee   :**  <br><br> 
    One Can send supporting fund through the "Buy us a Coffee" button, in the contact developers page in the app
 
# Help Required 
   The current OkHTTPclient handler in requests.java file accepts all unverified ssl certificates as the sophos
   certificate is not signed by a Certificate Authority. May be someone can come up with a way to handle them 
   with trustedSSL Certificates to make the app more secure. For Further understanding read the Disclaimer part and
   watch this video on [SSLCertificate Verification](https://www.youtube.com/watch?v=iQsKdtjwtYI)
    
# Disclaimer 
  * The sophos login page portal is not immune to phishing attack, this app overrides the requests made by
    webportal to perform "login" and "logout" operations, hence the app too is not immune to phishing attack
  
  * The above warning will be removed when the trustedSSLcertificate feature is added to the app, as the app will be immune
    to phishing attacks too by then

# License 
  The project falls under LGPL which conveys that one can use , and redistribute but should report all the changes made
  in the derived version

Wabalabdub!!

Smart Speaker
=============

## Overview ##




## Usage ##
0. Keep in mind that before next steps you should have Spotify client installed on your system, in case your system is Raspberry pi you should have raspotify installed (https://github.com/dtcooper/raspotify)

1. Clone the repository
    ```
    git clone https://github.com/ABckh/Smart-Speaker.git
    ```
   
2. Go inside cloned repository
    ```
    cd Smart-Speaker/
    ```
   
3. Open example.properties, and you should replace everything which is in curly brackets: <br />
      

      &nbsp; 3.1 &nbsp; Replace {your_picovoice_access_key}, to do so:<br />
            &nbsp; &nbsp; &nbsp; &nbsp;  3.1.1. &nbsp; Go to the Picovoice Console website: https://console.picovoice.ai/  <br />
            &nbsp; &nbsp; &nbsp; &nbsp;  3.1.2. &nbsp; If you don't have an account, click on the "Sign Up" button and create a new account. If you already have an account, log in.  <br />
            &nbsp; &nbsp; &nbsp; &nbsp;  3.1.3. &nbsp; Once you are logged in, you should see  'AccessKey' copy it and replace {your_picovoice_access_key} with your access key.  <br />
      ```
      picovoiceAccessKey={your_picovoice_access_key}
      ```
      
      
      &nbsp; 3.2 &nbsp; Replace {your_platform} with one of the options: linux, mac, raspberry-pi <br />
      ```
      porcupineKeywordPath=src/main/resources/porcupine_contexts/picovoice_{your_platform}.ppn
      ```
      ```
      rhinoContextPath=src/main/resources/rhino_contexts/Play-music-{your_platform}.rhn
      ```
      

      &nbsp; 3.3 Replace {your_spotify_client_id} and {your_spotify_client_secret} with Spotify client id and Spotify client secret, to do so: <br />
            &nbsp; &nbsp; &nbsp; &nbsp;  3.3.1. &nbsp; Go to the Spotify Developer Dashboard website: https://developer.spotify.com/dashboard/ <br />
            &nbsp; &nbsp; &nbsp; &nbsp;  3.3.2. &nbsp; Log in or sign up for a Spotify account. <br />
            &nbsp; &nbsp; &nbsp; &nbsp;  3.3.3. &nbsp; Click on the "Create an App" button. <br />
            &nbsp; &nbsp; &nbsp; &nbsp;  3.3.4. &nbsp; Fill in the details for your app, including its name and description. <br />
            &nbsp; &nbsp; &nbsp; &nbsp;  3.3.5. &nbsp; Once you have filled in the details, click on the "Create" button. <br />
            &nbsp; &nbsp; &nbsp; &nbsp;  3.3.6. &nbsp; You will now see the details of your app, including the Client ID and the Client Secret. Copy these values and paste them. <br />
      ```
      spotifyClientId={your_spotify_client_id}
      ```
      ```
      spotifyClientSecret={your_spotify_client_secret}
      ```

      &nbsp; 3.4 &nbsp; Replace {your_spotify_device_id} with your Spotify device ID, to do so: <br />
            &nbsp; &nbsp; &nbsp; &nbsp;  3.4.1. &nbsp; Go to the Spotify Developer website: https://developer.spotify.com/documentation/web-api/reference/get-a-users-available-devices <br />
            &nbsp; &nbsp; &nbsp; &nbsp;  3.4.2. &nbsp; Log in or sign up for a Spotify account. <br />
            &nbsp; &nbsp; &nbsp; &nbsp;  3.4.3. &nbsp; Click 'Try it' button<br />
            &nbsp; &nbsp; &nbsp; &nbsp;  3.4.4. &nbsp; In the 'Response' copy id of the device needed and paste it to the example.properties file<br />
      ```
      spotifyDeviceId={your_raspotify_device_id}
      ```
      
      &nbsp; 3.5 &nbsp; Replace {your_spotify_refresh_token} with Spotify refresh token, to do so: <br />
            &nbsp; &nbsp; &nbsp; &nbsp;  3.5.1. &nbsp; Go to the Spotify Web API Authorization Guide website: https://developer.spotify.com/documentation/general/guides/authorization-guide/ <br />
            &nbsp; &nbsp; &nbsp; &nbsp;  3.5.2. &nbsp; Follow the steps to obtain an Authorization Code.<br />
            &nbsp; &nbsp; &nbsp; &nbsp;  3.5.3. &nbsp; Once you have obtained the Authorization Code, use it to make a POST request to the Spotify Accounts service to obtain a Refresh Token. <br />
            &nbsp; &nbsp; &nbsp; &nbsp;  3.5.4. &nbsp; The Refresh Token should be returned in the response. Copy this value and paste it in example.properties <br />
      ```
      spotifyRefreshToken={your_spotify_refresh_token}
      ```   

4. Rename example.properties to config.properties
   ```
   mv example.properties config.properties
   ```

5. Install all the dependencies
    ```
    mvn clean install
    mvn compile 
    ```
   
6. Run the program:
    ```
   mvn exec:java
   ```

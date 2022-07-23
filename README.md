#Introduction

![alt text](./readmeimage.png)

2D Multiplayer Game called the Battle built using a Client-Server architecture. All inputs made from the clients (movement, attack, defense) are sent to
the server, and the server broadcasts them to all the clients connect to that server. Server also holds informations about clients connected, so in case 
a new client connects to the server after some clients have moved thourgh the screen or had their life % changed; The Server can then display the correct
state of the world/other clients for the new client that just connected.

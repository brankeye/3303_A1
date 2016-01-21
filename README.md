# 3303_A1

Names of files:

Server.java: 
	Responsible for determining a course of action based upon
	the Client's request forwarded through the IHost.
	Receives requests from the IntermediateHost on port 69.
	Sends replies to the IntermediateHost on port 68.
IntermediateHost.java: 
	Responsible for forwarding the Client's request to the
	Server and forwarding the Server's response to the Client.
	Receives requests from the Client on port 68.
	Sends requests to the Server on port 69.
Client.java:
	Responsible for making requests and receiving their responses.
	Receives requests from the IntermediateHost on port 68.
	Sends requests to the IntermediateHost on port 68.
	
To run:
	1) Right click Server.java, select Run as -> Java Application.
	2) Right click IntermediateHost.java, select Run as -> Java Application.
	3) Right click Client.java, select Run as -> Java Application.
# Smart Contract Browser Architecture
The top level architecture of the Smart Contract Browser is shown below.

![Top Level Architecture](/docs/architecture/architecture-top-level.png)

An author of a Smart Contract Application uses the authoring tool to create a smart contract, browser mark-up and script and pushes it to the blockchain. The author grants access to various users of the contract.

A user has a device and they install the Smart Contract Browser app. The app has a sandbox in which the browser mark-up and script execute. They user's keys are stored outside the sandbox. When the user first installs the app, if this is the first time they have used the app, they generate keys and the user's public key and encrypted private key are pushed to the cloud.
The app queries the cloud to see if the user is authorized for any additional contracts

The user's keys are securely synchronized across devices using a split key technology with a fixed password share using a modified Shamir Secret Split. The enrypted keys are stored on the blockchain.


Definitions:
* Smart Contract: The solidity code which resides on the blockchain.
* Smart Contract Application: A combination of the solidity code and the browser mark-up and script.

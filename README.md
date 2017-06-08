# Smart Contract Application Browser

The Smart Contract Application Browser (SCAB) aims to be a browser and app store for the Blockchain / Smart Contract era. A Smart Contract Application is my term for Decentralized Application (Dapp). This combines a code on a blockchain (smart contract), client side code and GUI, and cryptographic keys used to identify the parties involved in the contract.

The SCAB is an application which runs on an end point such as a mobile phone, tablet of computer. The Dapp GUI and client side code runs in a sandbox in the SCAB, in a similar way to how HTML and Javascript run in a sandbox in a web browser. Similarly to how web browsers can display content from many websites, the SCAB can display the GUI of many contracts.

The keys used to identify the user sit outside of the sandbox. When the SCAB interprets code in the Dapp client side code which executes a funciton call on the smart contract, the SCAB creates a blockchain transaction for the smart contract call and uses the user's key to sign the transaction. 

The goals of the SCAB are:

* Simpler creation process: Make it simpler for authors of a Smart Contract Applications to create and deploy new applications.
 * Problem: Smart Contract Application development is extremely complex and requires the development of mobile applications for each supported end-point. 
 * Goal: Allow technical people to create new applications without having to completely re-create mobile apps / client side pieces for each and every new application.
 * Goal: Allow non-technical people to create and deploy new applications based on a template.
* Limited access to keys:
 * Problem: All application end points currently need direct access to cryptographic keys. The keys must be manually shared between applications and end points. Manually sharing keys across many end points and applications risks key exposure.
 * Goal: Have Smart Contract Application clients run in a sandbox, and have them access keys via the sandbox. Allow multiple applications use the same sandbox.
 * Goal: Have keys shared securely between devices. 
* Complex security issues abstracted way from general application developers. This is the same problem as the limiting access to keys, but from a different perspective. 
 * Problem: In general, application developers do not have the expertise to make sound cryptographic and security decisions. Complex problems such as secure sharing of keys between devices, locking keys to hardware, and, choice of TLS configuration are unlikely to be fully addressed by application developers.
 * Goal: Develop a single application which solves complex security issues for application developers.

Further requirements:

Mobile app / end point:
* The mobile app provides access to all of the smart contracts that a user wants to use.
* The mobile app securely shares the users keys between the user's devices.
* The mobile app hosts Smart Contract Markup Language in a sandbox. The SCML could be similar to simple HTML. Buttons in the SCML can be linked to simple script code. The script code is interpreted and can include calls to the blockchain. These calls access the keys which are outside the sandbox.
* The commands available in the script code and the SCML must be simple to ensure it can be analyzed to ensure it doesn't break out of the sandbox.
* Allows users to be invited to use a (private) contract, or they can search for a (public) contract.


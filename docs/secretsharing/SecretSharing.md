# Secret Sharing Scheme

This page describes the proposed secret sharing scheme to be used to share information securely between devices.

## Secret Hierarchy
The hierarchy of secrets for SCAB is:
* User password and backup password, incombination with randomly generated secrets, which are used to protect:
  * Split secret which is used to protect:
    * Device specific asymmetric private keys, which are used to protect:
      * User keys and data.
   
## Split Secret Scheme Requirements
The requirements of the split secret scheme are:
* The secret is revealed if one of the following combinations is assembled:
  * User password, cloud secret, device secret  (normal scenario).
  * User password, cloud secret, back-up password (lost only device, recovery scenario)
  * User password, cloud secret, transfer secret (enrolling new device to share with)
* The cloud secret and device secrets must be able to be proactivized without changing the user password or the backup password.





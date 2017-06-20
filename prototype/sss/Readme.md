# Split Secret Scheme

The goal of the Split Secret Scheme is to allow data to be shared across 
devices while keeping the information secure.

## Requirements


Have:
* Password share: A share derived from the password via a password hardening algorithm. 
  Attributes: 
  * Derived from a user specified password.
  * User can change and automatically propogate across devices.
  * Is not changed for proactivization.
  * Is not changed for backup password change.
* Backup Password share: Same as Password Share. Should not be affected by 
  Password change.
  Attributes: 
  * Is not changed for proactivization.
  * Is not changed for password change.
* Device share: Randomly generated on device, never exported, but gets securely replicated across devices.
  Attributes:
  * Never changed.
* Cloud shares: stored in cloud. 
  * Changed to allow propogation of password and backup password changes.
  * Changed for proactivization, which occurs after the transfer share is generated.
* Transfer share: Used for enrolling a second device.
  * Generated on source device and entered via QR code scan on second device.

P: Prime number. All calculations are mod P.


Split secret recovery situations:
* Password Share, Cloud Share, Device Share (normal situation).
* Password Share, Cloud Share, Transfer Share (new device)
* Cloud Share, Device Share, Backup Share (forgotten password)
* Password Share, Cloud Share, Backup Share (lost last device).


## Equation
Equation to use: y = a*x^5 + b*x^4 + c*x^3 + d*x^2 + e*x + f mod p

6 share threshold. 
Cloud share is actually 4 shares.


Have:
* Secret: x=0
* Password Share: x=1
* Backup Share: x=3
* Device Share: x=2
* Transfer Share: x=10
* Cloud Share1: x=11
* Cloud Share2: x=12
* Cloud Share3: x=13
* Cloud Share4: x=14

## First Time Registration / Coefficient Generation:
Specify Password Share as yP, Backup Share yB.

~~~~
For x=1: yP = a1 + b1 + c1 + d1 + e1 + f1
For x=3: yB = a1*243 + b1*81 + c1*27 + d1*9 + e1*3 + f1
~~~~
Turning the first equation around:
~~~~
e1 = yP - a1 - b1 - c1 - d1 - f1
~~~~

Substituting in:
~~~~
yB = a1*243 + b1*81 + c1*27 + d1*9 + 3 * (yP - a1 - b1 - c1 - d1 - f1) + f1
yB = 3*yP + 240*a1 + 78*b1 + 24*c1 + 6*d1 - 2*f1
f1 = (3*yP + 240*a1 + 78*b1 + 24*c1 + 6*d1 - yB) / 2
~~~~
Hence:
~~~~
f1 = (3*yP + 240*a1 + 78*b1 + 24*c1 + 6*d1 - yB) / 2
a1 = randomly generated in random 1 to p-1.
b1 = randomly generated in random 1 to p-1.
c1 = randomly generated in random 1 to p-1.
d1 = randomly generated in random 1 to p-1.
e1 = yP - a1 - b1 - c1 - d1 - f1
~~~~



## Generic Change Equations
For proactivization, password change or backup password reset, the following needs to occur:
* Secret does not change.
* Device share does not change.
* Password share changes by mod difference between old and new password, or no change for proactivization or backup password change.
* Backup password share changes by mod difference between old and new backup password, or no change for proactivization or password change. 

Expressing this in equations:
~~~~
For secret, x=0, we want y=0, hence, 0=f2
For password share, x=1, we want y=dP, hence, dP = a2 + b2 + c2 + d2 + e2
For device share, x=2, we want y=0, hence, 0 = a2*32 + b2*16 + c2*8 + d2*4 + e2*2
For backup share, x=3, we want y=dB, hence, dB = a2*243 + b2*81 + c2*27 + d2*9 + e2*3
~~~~

Turning the first equation around:
~~~~
e2 = dP - a2 - b2 - c2 - d2
~~~~


Feeding into the second equation: 
~~~~
0 = a2*32 + b2*16 + c2*8 + d2*4 + e2*2
0 = a2*32 + b2*16 + c2*8 + d2*4 + 2 * (dP - a2 - b2 - c2 - d2)
0 = a2*30 + b2*14 + c2*6 + d2*2 + dP*2
d2  = -a2*15 - b2*7 - c2*3 - dP
~~~~
Feeding into the third equation: 
~~~~
dB = a2*243 + b2*81 + c2*27 + d2*9 + e2*3
dB = a2*243 + b2*81 + c2*27 - 9 * (a2*15 + b2*7 + c2*3 + dP) + 3 * (dP - a2 - b2 - c2 + (a2*15 + b2*7 + c2*3 + dP))
dB = a2*(243 - 9*15 - 3 + 3*15) + b2*(81 - 9*7 - 3 + 3*7) + c2*(27 - 9*3 - 3 +  3*3) + dP*(-9 + 3 +3)
dB = a2 * 150 + b2 * 36 + c2 * 6 - dP * 3
c2 = (dB - a2 * 150 - b2 * 36 + dP * 3) / 6
~~~~

Hence: 
~~~~
a2 = randomly generated in random 1 to p-1.
b2 = randomly generated in random 1 to p-1.
c2 = (dB - a2 * 150 - b2 * 36 + dP * 3) / 6
d2 = -a2*15 - b2*7 - c2*3 - dP
e2 = dP - a2 - b2 - c2 - d2
f2 = 0
~~~~

## Proactivization
Proactivization: Change the cloud shares so an old transfer share can not be with the cloud shares.
Secret, password share, backup share and device share do not change. 
The equations are the same as the generic equations, with dP and dB = 0.


## Password Change
Password change: Change the password. Have the password change propogate across devices.
Secret, backup share, and device share do not change. Password changes by delta of password share (difference between new and old share mod p).
The equations are the same as the generic equations, with dP = password change and dB = 0.


## Backup Password Change / Backup Password Reset
Backup password change: Change the backup password. 
Secret, password share, and device share do not change. 
Backup password changes by delta of password share (difference between new and old share mod p).
The equations are the same as the generic equations, with dP = 0 and dB = password change.


## Device Share Change
In this situation, change the secret as well and re-encrypt all data. This will be treated as a re-install, with all data re-encrypted. This will mean 
all other devices will need to be re-enrolled.



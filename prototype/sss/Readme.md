# Split Secret Scheme

The goal of the Split Secret Scheme is to allow data to be shared across 
devices while keeping the information secure.

## Requirements


Have:
* Password share: A share derived from the password via a password hardening algorithm. 
  Attributes: 
  * User can change and automatically propogate across devices.
  * Is not changed for proactivization.
  * Is not changed for backup password change.
* Cloud shares: stored in cloud.
* Backup Password share: Same as Password Share. Should not be affected by 
  Password change.
  Attributes: 
  * Is not changed for proactivization.
  * Is not changed for password change.
* Device share: One for each device.
  Attributes:
  * Can be proactivized.

P: Prime number. All calculations are mod P.


Split secret recovery situations:
* Password Share, Cloud Share, Device Share (normal situation).
* Password Share, Cloud Share, Transfer Share (new device)
* Cloud Share, Device Share, Backup Share (forgotten password)
* Password Share, Cloud Share, Backup Share (lost last device).


## Equation
Imagine: y = a*x**3 + b*x**2 + c*x + d

4 share threshold. 
Cloud share is actually 2 shares.

=> Password share, device share, and backup share can not be used.
=> Transfer share is transient.


Have:
 Secret: x=0
 Password Share: x=1
 Backup Share: x=-1

## Curve Generation:
Specify Password Share as yP, Backup Share yB.


y = a1*x^3 + b1*x^2 + c1*x + d1

For x=1: yP = a1 + b1 + c1 + d1

For x=-1: yB = -a1 + b1 - c1 + d1
 
a1 = yP - b1 - c1 - d1
and 
a1 = -yB + b1 - c1 + d1

yP - b1 - c1 - d1 = -yB + b1 - c1 + d1
yP + yB = 2*b1 + 2*d1
 
d1 = (yP + yB - 2*b1) / 2
Randomly generate a1, b1, and c1.


## Proactivization
Proactivization: no change secret, password, or backup share.
For secret, x=0, we want y=0, hence, 0=d2
For password share, x=1, we want y=0, hence, 0=a2+b2+c2+d2
For backup share, x=-1, we want y=0, hence, 0=-a2+b2-c2+d2

a2 + b2 + c2 = -a2 + b2 -c2
2*a2 = -2*c2
a2 = -c2

Proactivization equation:
y = a2*x^3 - a2*x


## Password Change
Password change: no change secret, or backup share. Password changes by 
delta of share
 
For secret, x=0, we want y=0, hence, 0=d3
For password share, x=1, we want y=yP3 (the modP 
difference of the passwords), hence, yP3=a3+b3+c3
For backup share, x=-1, we want y=0, hence, 0=-a3+b3-c3

a3 = yP3 -b3 -c3
a3 = b3 -c3
yP3 - b3 -c3 = b3 - c3
b3 = yP3/2

Substituting back in:
a3 = yP3/2 -c3

Password Change Equation:
y = (yP3/2 - c3) * x^3 + yP3/2 * x^2 + c3 * x 


## Backup Password Change
Backup Password change: no change secret, or backup share. Backup 
Password changes by delta of share
 
For secret, x=0, we want y=0, hence, 0=d4
For password share, x=1, we want y=0, hence, 0=a4+b4+c4
For backup share, x=-1, we want y=yB4, hence, yB4=-a4+b4-c4

a4 = -b4 -c4
a4 = b4 -c4 - yB4
-b4 -c4 = b4 - c4 - yB4
b4 = -yB4/2

Substituting back in:
a4 = -yB4/2 -c4

Password Change Equation:
y = (-yP4/2 - c4) * x^3 - yP4/2 * x^2 + c4 * x 



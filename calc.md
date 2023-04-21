# calc

overlap >= 0.25 | ]0..1[
lImage = 20
(lPano = 80) 

------

lOverlap = lImage * overlap | 5 

lImageA = lImage - lOverlap | 15
lImageB = lImage - 2 * lOverlap | 10

n = 5
lPano = (2 * lImageA) + ((n-2) * lImageB) + ((n-1) * lOverlap) | 30 + 30 + 20 = 80

n = 6
lPano = (2 * lImageA) + ((n-2) * lImageB) + ((n-1) * lOverlap) | 30 + 40 + 25 = 95

---- 

lPano = (2 * (lImage * (1-lOverlap)))
      + ((n-2) * (lImage - (1-lOverlap - lOverlap) )) 
      + ((n-1) * lImage *lOverlap) | 30 + 40 + 25 = 95

short:
p = (2 * (i * (1-o))) + ((n-2) * (i * (1-o-o))) + ((n-1) * (i*o)) 
p = 30 + 30 + 20 = 80
---
o = (-i*n+p) / (-i*n+i)

i = 20; n=5; p=80; (o=0.25)
o = (-100 + 80) / (-100+20) = -20 / -80 = 0.25 !! 
i = 20; n=6; p=80; (o=0.25)
o = (-120 + 80) / (-120+20) = -40 / -100 = 0.4 seems ok 

----
n = (-i*o + p) / (-i * o + i)

i = 20; p = 80 ; o=0.25
n = 75 / 15 = 5

i = 20; p = 80 ; o=0.33
n = (-6.6 + 80) / (-6.6) + 20 = 73.4 / 13,4 = 5,4776119402985074626865671641791 -> 6 

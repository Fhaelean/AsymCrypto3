package com.company;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


public class Main
{
    public static void main(String[] args)
    {
        List<BigInteger> keyA = GenerateKeyPair();
        BigInteger p = keyA.get(0);
        BigInteger q = keyA.get(1);
        BigInteger n = keyA.get(2);
        System.out.println("N = " + hex(n));
        Scanner in = new Scanner(System.in);
        int switch1 = 0;
        while (switch1 != 7)
        {
            showMenu();
            switch1 = in.nextInt();
            switch (switch1)
            {
                case 1:
                    Encrypt(n);
                    break;
                case 2:
                    Decrypt(p, q);
                    break;
                case 3:
                    Sign(n, p, q);
                    break;
                case 4:
                    Verify(n);
                    break;
                case 5:
                {
                    List<BigInteger> proto = ProtoAlicestart(n);
                    BigInteger y = proto.get(1);
                    BigInteger x = proto.get(0);
                    BigInteger z = ProtoBob(p,q,y,n);
                    // System.out.println(z);
                    ProtoAliceEnd(z,n,x);
                }
                break;
                case 6:
                    attakprot();
                    break;
                default:
                    if (switch1 != 7)
                        System.out.println("Error try again");
                    break;
            }
        }
    }

     static BigInteger Formatmessage(BigInteger n, BigInteger message)
    {
        int l = n.bitLength()/8;
        if (message.bitLength()/8+1 > l-10) throw new IllegalArgumentException();
        BigInteger preparedMessage = BigInteger.ZERO;
        BigInteger r = new BigInteger(64, new Random());
        //System.out.println(r.toString(16));
        preparedMessage = BigInteger.valueOf(255).multiply(BigInteger.valueOf(2).pow(8*(l-2)))
                .add(message.multiply(BigInteger.valueOf(2).pow(64))).add(r);

        return preparedMessage;
    }

    static List GenerateKeyPair()
    {
        BigInteger p = BigInteger.ZERO;
        BigInteger q = BigInteger.ZERO;
        int i = 0;
        int j = 2;
        while(j != i)
        {
            p = BigPrime();
            q = BigPrime();
            if (i != 1)
            {
                if ((p.mod(BigInteger.valueOf(4))).compareTo(BigInteger.valueOf(3)) == 0)
                    i = 1;
            }
            if (j != 1)
            {
                if ((q.mod(BigInteger.valueOf(4))).compareTo(BigInteger.valueOf(3)) == 0)
                    j = 1;
            }
        }
        List<BigInteger> keyPairs = new ArrayList<BigInteger>();
        keyPairs.add(p);
        keyPairs.add(q);
        keyPairs.add(p.multiply(q));
        return keyPairs;
    }

    static BigInteger BigPrime()
    {
        int[] l89 = L89();
        BigInteger a = BigInteger.ZERO;
        String str = "";
        for (int i = 0; i < l89.length; i++)
        {
            if(i % 128 == 0 && i != 0)
            {
                a = new BigInteger(str, 2);
                Boolean res = MillerRabinTest(a , 1024);
                if(res == true && a.bitLength() == 128)
                    break;
                str = "";
            }
            str += l89[i];
        }
        return a;
    }

    static int[] L89()
    {
        int[] L89 = new int[8000000];
        for (int i = 0; i < 89; i++)
            L89[i] = (int)(Math.random() * 2);
        for (int i = 0; i < (L89.length - 89); i++)
            L89[89 + i] = (L89[i] ^ L89[i + 51]);
        return L89;
    }

    static Boolean MillerRabinTest(BigInteger p, int k)
    {
        if (p.compareTo(BigInteger.valueOf(2)) == -1)
            return false;
        if (p.compareTo(BigInteger.valueOf(2)) == 0)
            return true;
        if (p.mod(BigInteger.valueOf(2)) == BigInteger.ZERO)
            return false;

        BigInteger zero = BigInteger.ZERO;
        BigInteger one = BigInteger.ONE;
        BigInteger r = zero;
        BigInteger d = p.subtract(one);

        while (d.mod(BigInteger.valueOf(2)) == zero)
        {
            d = d.divide(BigInteger.valueOf(2));
            r = r.add(one);
        }

        for (int i = 0; i < k; i++)
        {
            BigInteger a = nextRandomBigInteger(p.subtract(BigInteger.valueOf(2)));
            BigInteger x = a.modPow(d, p);
            if (x.compareTo(one) == 0 || x.compareTo(p.subtract(one)) == 0)
                continue;
            for (int j = 0; BigInteger.valueOf(j).compareTo(r.subtract(one)) < 0; j++)
            {
                x = x.modPow(BigInteger.valueOf(2), p);
                if (x.compareTo(one) == 0)
                    return false;
                if (x.compareTo(p.subtract(one)) == 0)
                    break;
            }
            if (x.compareTo(p.subtract(one)) != 0)
                return false;
        }
        return true;
    }

    static BigInteger nextRandomBigInteger(BigInteger n)
    {
        Random rand = new Random();
        BigInteger zero = BigInteger.ZERO;
        BigInteger result = new BigInteger(n.bitLength(), rand);
        while( result.compareTo(n) >= 0 )
            result = new BigInteger(n.bitLength(), rand);
        if(result != zero || result != BigInteger.ONE || result != BigInteger.valueOf(2))
            return result;
        return zero;
    }

    static BigInteger enterMsg()
    {
        Scanner in = new Scanner(System.in);
        BigInteger message = in.nextBigInteger();
        return message;
    }

    static String hex(BigInteger val)
    {
        String strval = val.toString(16);
        return strval;
    }

    static BigInteger Decrypt(BigInteger p, BigInteger q)
    {
        Scanner in = new Scanner(System.in);
        BigInteger n = p.multiply(q);

        System.out.println("");

        System.out.println("Enter y = ");
        String str1 = in.nextLine();
        BigInteger y = new BigInteger(str1, 16);
        System.out.println("Enter b = ");
        String str2 = in.nextLine();
        BigInteger b = new BigInteger(str2, 16);
        System.out.println("Enter c1 = ");
        BigInteger C1 = enterMsg();
        System.out.println("Enter c2 = ");
        BigInteger C2 = enterMsg();

        y = y.add(b.multiply(b).multiply(BigInteger.valueOf(4).modInverse(n))).mod(n);

        BigInteger pow1 = (p.add(BigInteger.ONE)).divide(BigInteger.valueOf(4));
        BigInteger pow2 = (q.add(BigInteger.ONE)).divide(BigInteger.valueOf(4));
        BigInteger s1 = y.modPow(pow1, p);
        BigInteger s2 = y.modPow(pow2, q);

        List<BigInteger> gcd = gcdExt(p, q);
        BigInteger d = gcd.get(0);
        BigInteger u = gcd.get(1);
        BigInteger v = gcd.get(2);
        if(d.compareTo(BigInteger.ONE) != 0)
        {
            System.out.println("Error: GCD != 1");
            return null;
        }
        BigInteger x11 = u.multiply(p).multiply(s2).add(v.multiply(q).multiply(s1)).mod(n);
        BigInteger x1 = (b.multiply(BigInteger.valueOf(2).modInverse(n)).negate()).add(x11).mod(n);

        BigInteger x22 = u.multiply(p).multiply(s2).subtract(v.multiply(q).multiply(s1)).mod(n);
        BigInteger x2 = (b.multiply(BigInteger.valueOf(2).modInverse(n)).negate()).add(x22).mod(n);

        BigInteger x33 = (u.multiply(p).multiply(s2).negate()).add(v.multiply(q).multiply(s1)).mod(n);
        BigInteger x3 = (b.multiply(BigInteger.valueOf(2).modInverse(n)).negate()).add(x33).mod(n);

        BigInteger x44 = (u.multiply(p).multiply(s2).negate()).subtract(v.multiply(q).multiply(s1)).mod(n);
        BigInteger x4 = (b.multiply(BigInteger.valueOf(2).modInverse(n)).negate()).add(x44).mod(n);

        BigInteger two = BigInteger.valueOf(2);

        BigInteger cx1 = (x1.add(b.multiply(two.modInverse(n))).mod(n)).mod(two);
        BigInteger cx2 = (x2.add(b.multiply(two.modInverse(n))).mod(n)).mod(two);
        BigInteger cx3 = (x3.add(b.multiply(two.modInverse(n))).mod(n)).mod(two);
        BigInteger cx4 = (x4.add(b.multiply(two.modInverse(n))).mod(n)).mod(two);

        BigInteger c2bx1 = x1.add(b.multiply(two.modInverse(n)));
        BigInteger c2bx2 = x2.add(b.multiply(two.modInverse(n)));
        BigInteger c2bx3 = x3.add(b.multiply(two.modInverse(n)));
        BigInteger c2bx4 = x4.add(b.multiply(two.modInverse(n)));

        BigInteger ya1 = BigInteger.valueOf(Yakobi(c2bx1,n));
        BigInteger ya2 = BigInteger.valueOf(Yakobi(c2bx2,n));
        BigInteger ya3 = BigInteger.valueOf(Yakobi(c2bx3,n));
        BigInteger ya4 = BigInteger.valueOf(Yakobi(c2bx4,n));

        BigInteger c21, c22, c23, c24;

        if (ya1.compareTo(BigInteger.ONE) == 0)
            c21 = BigInteger.valueOf(1);
        else
            c21 = BigInteger.valueOf(0);
        if (ya2.compareTo(BigInteger.ONE) == 0)
            c22 = BigInteger.valueOf(1);
        else
            c22 = BigInteger.valueOf(0);
        if (ya3.compareTo(BigInteger.ONE) == 0)
            c23 = BigInteger.valueOf(1);
        else
            c23 = BigInteger.valueOf(0);
        if (ya4.compareTo(BigInteger.ONE) == 0)
            c24 = BigInteger.valueOf(1);
        else
            c24 = BigInteger.valueOf(0);

        System.out.println();
        if(C1.compareTo(cx1) == 0 && C2.compareTo(c21) == 0)
        {
            System.out.println("x = " + x1);
            getByte(x1);
            return null;
        }
        if(C1.compareTo(cx2) == 0 && C2.compareTo(c22) == 0)
        {
            System.out.println("x = " + x2);
            getByte(x2);
            return null;
        }
        if(C1.compareTo(cx3) == 0 && C2.compareTo(c23) == 0)
        {
            System.out.println("x = " + x3);
            getByte(x3);
            return null;
        }
        if(C1.compareTo(cx4) == 0 && C2.compareTo(c24) == 0)
        {
            System.out.println("x = " + x4);
            getByte(x4);
            return null;
        }
        return null;
    }

    static void getByte(BigInteger x)
    {
        byte[] arr = x.toByteArray();
        byte[] res = new byte[arr.length - 10];
        int j = 0;
        for (int i = 2; i < arr.length - 8; i++)
            res[j++] = arr[i];
        System.out.println();
        BigInteger resul = new BigInteger(res);
        System.out.println("result = " + resul);
        System.out.println();
    }

    static List gcdExt(BigInteger a, BigInteger b)
    {
        List<BigInteger> gcd = new ArrayList<BigInteger>();
        int ij = 0;
        if (a.compareTo(b) == 1)
            ij = 1;
        if (a.compareTo(b) == -1)
            ij = -1;

        BigInteger q, r, x2, x1, y2, y1;
        BigInteger zero = BigInteger.ZERO;
        BigInteger one = BigInteger.ONE;
        BigInteger d = zero;
        BigInteger x = zero;
        BigInteger y = zero;

        x1 = zero; x2 = one; y1 = one; y2 = zero;
        while (b.compareTo(zero) == 1)
        {
            q = a.divide(b);
            r = a.subtract(q.multiply(b));
            x = x2.subtract(q.multiply(x1));
            y = y2.subtract(q.multiply(y1));
            a = b;
            b = r;
            x2 = x1;
            x1 = x;
            y2 = y1;
            y1 = y;
        }
        if (ij == -1)
        {
            d = a;
            x = x2;//small
            y = y2;//big
            gcd.add(d);
            gcd.add(x);
            gcd.add(y);
        }
        if (ij == 1)
        {
            d = a;
            x = y2;//small
            y = x2;//big
            gcd.add(d);
            gcd.add(y);
            gcd.add(x);
        }
        return gcd;
    }

    static void Encrypt(BigInteger n)
    {
        /*Scanner in = new Scanner(System.in);
        System.out.println("enter n");
        String str1 = in.nextLine();
        n = new BigInteger(str1, 16);*/

        System.out.println("Enter message = ");
        BigInteger m = enterMsg();
        BigInteger x = Formatmessage(n, m);

        BigInteger b = new BigInteger(64,new Random());

        /*System.out.println("enter b");
        String str2 = in.nextLine();
        b = new BigInteger(str2, 16);*/

        BigInteger two = BigInteger.valueOf(2);
        BigInteger y = (x.multiply(x.add(b))).mod(n);
        BigInteger c1 = (x.add(b.multiply(two.modInverse(n))).mod(n)).mod(two);
        BigInteger c2b = x.add(b.multiply(two.modInverse(n)));

        if(c2b.gcd(n).compareTo(BigInteger.ONE) != 0)
            return;

        BigInteger yakobiSymb = BigInteger.valueOf(Yakobi(c2b,n));
        BigInteger c2;

        if (yakobiSymb.compareTo(BigInteger.ONE) == 0)
            c2 = BigInteger.valueOf(1);
        else
            c2 = BigInteger.valueOf(0);
        System.out.println("x = " + x);
        System.out.println("Y = " + y + "\n" + "b = " + b + "\n" + "C1 = " + c1 + " C2 = " + c2);
        System.out.println("Yhex = " + hex(y));
    }

    static int Yakobi(BigInteger A , BigInteger B)
    {
        BigInteger ZERO = BigInteger.valueOf(0);
        BigInteger ONE = BigInteger.valueOf(1);
        BigInteger TWO = BigInteger.valueOf(2);

        int[] jacobiTable = {0, 1, 0, -1, 0, -1, 0, 1};

        BigInteger a, b, v;
        long k = 1;

        if (B.equals(ZERO))
        {
            a = A.abs();
            return a.equals(ONE) ? 1 : 0;
        }

        if (!A.testBit(0) && !B.testBit(0))
            return 0;

        a = A;
        b = B;

        if (b.signum() == -1)
        {
            b = b.negate();
            if (a.signum() == -1)
                k = -1;
        }

        v = ZERO;
        while (!b.testBit(0))
        {
            v = v.add(ONE);
            b = b.divide(TWO);
        }

        if (v.testBit(0))
            k = k * jacobiTable[a.intValue() & 7];

        if (a.signum() < 0)
        {
            if (b.testBit(1))
            {
                k = -k;
            }
            a = a.negate();
        }

        while (a.signum() != 0)
        {
            v = ZERO;
            while (!a.testBit(0))
            {
                v = v.add(ONE);
                a = a.divide(TWO);
            }
            if (v.testBit(0))
                k = k * jacobiTable[b.intValue() & 7];

            if (a.compareTo(b) < 0)
            {
                BigInteger x = a;
                a = b;
                b = x;
                if (a.testBit(1) && b.testBit(1))
                    k = -k;
            }
            a = a.subtract(b);
        }
        return b.equals(ONE) ? (int)k : 0;
    }

    static void Sign(BigInteger n, BigInteger p, BigInteger q)
    {
        System.out.println("Enter message = ");
        BigInteger m = enterMsg();
        BigInteger x = Formatmessage(n, m);

        BigInteger pow11 = (p.subtract(BigInteger.ONE)).divide(BigInteger.valueOf(2));
        BigInteger pow22 = (q.subtract(BigInteger.ONE)).divide(BigInteger.valueOf(2));

        BigInteger aa = x.modPow(pow11, p);
        BigInteger aa1 = x.modPow(pow22, q);

        while((aa.compareTo(BigInteger.ONE) != 0) || (aa1.compareTo(BigInteger.ONE) != 0))
        {
            x = Formatmessage(n, m);
            aa = x.modPow(pow11, p);
            aa1 = x.modPow(pow22, q);
        }

        BigInteger pow1 = (p.add(BigInteger.ONE)).divide(BigInteger.valueOf(4));
        BigInteger pow2 = (q.add(BigInteger.ONE)).divide(BigInteger.valueOf(4));
        BigInteger s1 = x.modPow(pow1, p);
        BigInteger s2 = x.modPow(pow2, q);

        List<BigInteger> gcd = gcdExt(p, q);
        BigInteger u = gcd.get(1);
        BigInteger v = gcd.get(2);

        BigInteger sqrt = u.multiply(p).multiply(s2).add(v.multiply(q).multiply(s1)).mod(n);

        System.out.println("mod = " + hex(n));
        System.out.println("msg = " + hex(x));
        System.out.println("sign = " + hex(sqrt));
    }

    static void Verify(BigInteger n)
    {
        Scanner in = new Scanner(System.in);
        System.out.println("enter n");
        String str1 = in.nextLine();
        n = new BigInteger(str1, 16);

        System.out.println("Enter m = ");
        BigInteger m = enterMsg();
        System.out.println("Enter s = ");

        String str2 = in.nextLine();
        BigInteger s = new BigInteger(str2, 16);

        BigInteger xres = s.modPow(BigInteger.valueOf(2), n);

        byte[] arr = xres.toByteArray();
        byte[] res = new byte[arr.length - 10];
        int j = 0;
        for (int i = 2; i < arr.length - 8; i++)
            res[j++] = arr[i];
        System.out.println();
        BigInteger resul = new BigInteger(res);

        if(resul.equals(m))
            System.out.println("Sign OK");
        else
            System.out.println("Error");

    }

    static void showMenu()
    {
        System.out.println("1 - to encrypt");
        System.out.println("2 - to decrypt");
        System.out.println("3 - to sign");
        System.out.println("4 - to verify");
        System.out.println("5 - protokol proof of knowledge");
        System.out.println("6 - atack");
        System.out.println("7 - to exit");
    }

    static List ProtoAlicestart(BigInteger n)
    {
        List<BigInteger> alice = new ArrayList<BigInteger>();
        BigInteger four = BigInteger.valueOf(4);
        BigInteger x = new BigInteger(64,new Random());
        while(x.bitLength() != 64)
            x = new BigInteger(64,new Random());
        BigInteger y = x.modPow(four,n);
        alice.add(x);
        alice.add(y);
        return alice;
    }

    static BigInteger ProtoBob(BigInteger p , BigInteger q, BigInteger y, BigInteger n)
    {
        BigInteger pow1 = (p.add(BigInteger.ONE)).divide(BigInteger.valueOf(4));
        BigInteger pow2 = (q.add(BigInteger.ONE)).divide(BigInteger.valueOf(4));
        BigInteger s1 = y.modPow(pow1, p);
        BigInteger s2 = y.modPow(pow2, q);
        List<BigInteger> gcd = gcdExt(p, q);
        BigInteger u = gcd.get(1);
        BigInteger v = gcd.get(2);

        BigInteger sqrt1 = u.multiply(p).multiply(s2).add(v.multiply(q).multiply(s1)).mod(n);
        BigInteger sqrt2 = u.multiply(p).multiply(s2).subtract(v.multiply(q).multiply(s1)).mod(n);
        BigInteger sqrt3 = (u.multiply(p).multiply(s2).negate()).add(v.multiply(q).multiply(s1)).mod(n);
        BigInteger sqrt4 = (u.multiply(p).multiply(s2).negate()).subtract(v.multiply(q).multiply(s1)).mod(n);

        BigInteger yakobiSymbsqrt1 = BigInteger.valueOf(Yakobi(sqrt1, n));
        BigInteger yakobiSymbsqrt2 = BigInteger.valueOf(Yakobi(sqrt2, n));
        BigInteger yakobiSymbsqrt3 = BigInteger.valueOf(Yakobi(sqrt3, n));
        BigInteger yakobiSymbsqrt4 = BigInteger.valueOf(Yakobi(sqrt4, n));
        BigInteger result;
        if(yakobiSymbsqrt1.equals(BigInteger.ONE))
        {
            result = sqrt1;
            return  result;
        }
        if(yakobiSymbsqrt2.equals(BigInteger.ONE))
        {
            result = sqrt2;
            return  result;
        }
        if(yakobiSymbsqrt3.equals(BigInteger.ONE))
        {
            result = sqrt3;
            return  result;
        }
        if(yakobiSymbsqrt4.equals(BigInteger.ONE))
        {
            result = sqrt4;
            return  result;
        }
        return BigInteger.valueOf(0);
    }

    static void ProtoAliceEnd(BigInteger z ,BigInteger n, BigInteger x)
    {
        BigInteger two = BigInteger.valueOf(2);
        BigInteger z1 = x.modPow(two,n);
        if(z.equals(z1))
        {
            System.out.println("Bob know p and q ");
        }
        else
        {
            System.out.println("Oh my God... Bob try deceive you. Dial 911");
        }
    }

    static void attakprot()
    {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter mod = ");
        String str = in.nextLine();
        BigInteger n = new BigInteger(str, 16);
        System.out.println("N = " + n);

        BigInteger t = new BigInteger(64,new Random());
        BigInteger y = t.modPow(BigInteger.valueOf(2), n);
        System.out.println("Y = " + hex(y));

        System.out.println("Z = ");
        String str2 = in.nextLine();
        BigInteger z = new BigInteger(str2, 16);
        if(t.equals(z) || t.equals(z.negate()))
            System.out.println("t = +- z");
        else
        {
            List<BigInteger> dil = gcdExt(t.add(z),n);
            BigInteger d = dil.get(0);
            System.out.println("p = " + d);
            System.out.println("q = " + n.divide(d));
            System.out.println("N = " + (d.multiply(n.divide(d))));

        }
    }
}

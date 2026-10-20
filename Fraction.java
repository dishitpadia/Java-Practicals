public class Fraction {
    private int num, den;

    Fraction(int num, int den)
    {
        int g = gcd(num, den);
        this.num = num / g;
        this.den = den / g;
    }

    public int gcd(int num, int den) {

        while (den != 0) {
            int rem = num % den;
            num = den;
            den = rem;
        }
        return num;
    }

    @Override
    public String toString() {
        return (num + "/" + den);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        Fraction other = (Fraction) obj;

        return num == other.num && den == other.den;
    }
}

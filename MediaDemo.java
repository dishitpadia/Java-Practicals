abstract class Media {
    int daysLate;

    public Media(int days) {
        daysLate = days;
    }

    abstract double calculateLateFees();

    String getMediaType() {
        return "Media";
    }
}

class Book extends Media {
    
    int LateFees;

    public Book(int days) {
        super(days);
        LateFees = 2;
    }

    public double calculateLateFees() {
        return daysLate * LateFees;
    }

    public String getMediaType() {
        return "Book";
    }
}

class videoGame extends Media {
    
    int LateFees;

    public videoGame(int days) {
        super(days);
        LateFees = 10;
    }

    public double calculateLateFees() {
        return daysLate * LateFees;
    }

    public String getMediaType() {
        return "Video Game";
    }
}

class CD extends Media {
    
    int LateFees;

    public CD(int days) {
        super(days);
        LateFees = 5;
    }

    public double calculateLateFees() {
        return daysLate * LateFees;
    }

    public String getMediaType() {
        return "CD";
    }
}

public class MediaDemo {
    public static void main(String[] args) {
        Media[] medias = {new Book(3), new videoGame(2), new CD(4),
            new Book(5), new CD(1), new videoGame(3)
        };

        for(int i = 0; i < medias.length; ++i) {
            System.out.println("The late fees for " + medias[i].getMediaType() + " is: " + medias[i].calculateLateFees());
        }
    }    
}

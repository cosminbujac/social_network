package socialnetwork.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Prietenie extends Entity<Tuple<Long,Long>> {
    Long idU1,idU2;
    LocalDateTime date;

    public Prietenie(Long u1, Long u2) {
        this.idU1 = u1;
        this.idU2 = u2;
        this.date = LocalDateTime.now();

    }

    public Long getIdU1() {
        return idU1;
    }

    public void setIdU1(Long idU1) {
        this.idU1 = idU1;
    }

    public Long getIdU2() {
        return idU2;
    }

    public void setIdU2(Long idU2) {
        this.idU2 = idU2;
    }

    @Override
    public String toString() {
        return "Utilizatorul cu id-ul " + idU1 +
                " s-a imprietenit cu utilizitatorul cu id-ul " + idU2 +
                " la data " + this.date.format( DateTimeFormatter.ofPattern("dd-MM-YYYY"));
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }
}

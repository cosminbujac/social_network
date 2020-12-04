package socialnetwork.domain.validators;

import socialnetwork.domain.Prietenie;

public class PrietenieValidator implements Validator<Prietenie>{

    @Override
    public void validate(Prietenie entity) throws ValidException {
        if(entity.getIdU1()==null || entity.getIdU2()==null)
            throw new ValidException("Utilizatorii nu pot avea id-uri nule!");


    }
}

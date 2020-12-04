package socialnetwork.domain.validators;

import socialnetwork.domain.Utilizator;

public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    public void validate(Utilizator entity) throws ValidException {
        if(entity.getLastName().isEmpty()) {
            throw new ValidException("Invalid lastname!");
        }
        if(entity.getFirstName().isEmpty()){
            throw new ValidException("Invalid firstname!");
        }
        if(!entity.getLastName().chars().allMatch(Character::isLetter))
            throw new ValidException("Lastname has invalid chatacters");
        if(!entity.getFirstName().chars().allMatch(Character::isLetter))
            throw new ValidException("Firstname has invalid characters!");
        if(entity.getFirstName().length() > 100)
        {
            throw new ValidException("Firstname is too long!");
        }
        if(entity.getLastName().length() > 100)
        {
            throw new ValidException("Lastname is too long!");
        }
    }
}

package com.neptune.api.template.domain;

import java.lang.reflect.Field;
import java.security.SecureRandom;

import javax.xml.bind.ValidationException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Domain Template class that MUST be implemented to ensure type, and to be used
 * in generics :)
 * 
 * @author Rafael Rabelo
 *
 */
public abstract class DomainTemplate {

    /**
     * The number of random digits in the randomId to be used to compose
     * resourceId
     */
    public static int RANDOM_SIZE = 7;

    /**
     * Can't create a composed pk (@IdClass) with generated field and custom
     * generators. - Generators are cool
     * (http://blog.anorakgirl.co.uk/2009/01/custom-hibernate-sequence-generator-for-id-field/)
     * - But they are ONLY to Id fields - And yeah, you can't use composed keys
     * and generators: "Warning: When your class implements the composite
     * primary-key it will not be able to use the sequence id generation. You
     * will have to generate the ID by your software code."
     * (http://uaihebert.com/tutorial-jpa-composite-primary-key/)
     * 
     * The id HAS to be generated because is auto increasing while the randomId
     * is there just to create a not predictable id behavior.
     */
    public DomainTemplate() {
        SecureRandom random = new SecureRandom();

        StringBuilder sb = new StringBuilder(DomainTemplate.RANDOM_SIZE);
        for (int i = 0; i < DomainTemplate.RANDOM_SIZE; i++) {
            sb.append(random.nextInt(10));
        }

        this.setRandomId(sb.toString());
    }

    /**
     * Gets the Integer Id part of resourceId DOES *NOT* GET XML SERIALIZED
     * 
     * @return Integer Id
     */
    @XmlTransient
    public abstract Integer getId();

    /**
     * Sets the Integer Id part of resourceId
     * 
     * @param id
     *            the Integer id
     */
    public abstract void setId(Integer id);

    /**
     * A random Id to be used as "kind of" composed pk with the primary key Id
     * 
     * @see DomainTemplate#RANDOM_SIZE It is a String because in this case, 0
     *      DOES MATTER! DOES *NOT* GET XML SERIALIZED
     * 
     *      Could be even a-z, A-Z, 0-9, instead of 0-9. Hummm... TODO: this!
     * @return 'n' char String, not unique, randomly generated, where 'n' is
     *         equals to @see DomainTemplate.randomSize
     */
    @XmlTransient
    public abstract String getRandomId();

    /**
     * Sets the 'n' char randomId. It is NOT implemented because it should
     * persist.
     * 
     * @param randomId
     *            'n' char String where 'n' should be equals to @see
     *            DomainTemplate.randomSize
     */
    public abstract void setRandomId(String randomId);

    /**
     * Used to mask the real Id of the Resource. Using this, an attacker will
     * never know the URL to a Resource even if he knows the Id, that is auto
     * incremented. Example: If there is an Id 26, it is probable that there is
     * at least one Id between 1 and 26 that is also valid, and that could be
     * outside of his access level. But, if the Resource has a resourceId equals
     * to 2113412326, even if he knows that the 26 is the true Id, he wouldn't
     * know the first 7 chars of the other Resources, and it would take 10^7 *
     * 25 attempts to guarantee that he would find ONE valid Resource.
     * 
     * @return RandomId concatenated with Id
     */
    @XmlElement(name = "resource")
    public final String getResourceId() {
        return getRandomId() + getId();
    }

    /**
     * We should be able to set a resourceId, even if it is not persistent,
     * because we can decompose it to a Id and randomId search in the database.
     * 
     * @param resourceId
     *            The Id following the rules of size of each component
     * @throws ValidationException
     *             if the resourceId size is smaller then the number of random
     *             digits
     */
    public final void setResourceId(String resourceId)
            throws ValidationException {
        if (resourceId.length() < DomainTemplate.RANDOM_SIZE) {
            throw new ValidationException("The resource Id is invalid");
        }
        this.setRandomId(resourceId.substring(0, DomainTemplate.RANDOM_SIZE));
        this.setId(Integer
                .parseInt(resourceId.substring(DomainTemplate.RANDOM_SIZE)));
    }

    public void copy(Object t) throws IllegalArgumentException {
        if (this.getClass() == t.getClass()) {
            for (Field field : t.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    field.set(this, field.get(t));
                } catch (IllegalAccessException e) {
                }
            }
        }
    }
}

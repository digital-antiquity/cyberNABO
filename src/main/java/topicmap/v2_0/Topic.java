//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.01.18 at 09:17:02 AM MST 
//


package topicmap.v2_0;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://www.topicmaps.org/xtm/}itemIdentity"/>
 *           &lt;element ref="{http://www.topicmaps.org/xtm/}subjectLocator"/>
 *           &lt;element ref="{http://www.topicmaps.org/xtm/}subjectIdentifier"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.topicmaps.org/xtm/}instanceOf" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://www.topicmaps.org/xtm/}name"/>
 *           &lt;element ref="{http://www.topicmaps.org/xtm/}occurrence"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "itemIdentityOrSubjectLocatorOrSubjectIdentifier",
    "instanceOf",
    "nameOrOccurrence"
})
@XmlRootElement(name = "topic")
public class Topic {

    @XmlElements({
        @XmlElement(name = "itemIdentity", type = ItemIdentity.class),
        @XmlElement(name = "subjectLocator", type = SubjectLocator.class),
        @XmlElement(name = "subjectIdentifier", type = SubjectIdentifier.class)
    })
    protected List<Object> itemIdentityOrSubjectLocatorOrSubjectIdentifier;
    protected InstanceOf instanceOf;
    @XmlElements({
        @XmlElement(name = "name", type = Name.class),
        @XmlElement(name = "occurrence", type = Occurrence.class)
    })
    protected List<Object> nameOrOccurrence;
    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Gets the value of the itemIdentityOrSubjectLocatorOrSubjectIdentifier property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the itemIdentityOrSubjectLocatorOrSubjectIdentifier property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItemIdentityOrSubjectLocatorOrSubjectIdentifier().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ItemIdentity }
     * {@link SubjectLocator }
     * {@link SubjectIdentifier }
     * 
     * 
     */
    public List<Object> getItemIdentityOrSubjectLocatorOrSubjectIdentifier() {
        if (itemIdentityOrSubjectLocatorOrSubjectIdentifier == null) {
            itemIdentityOrSubjectLocatorOrSubjectIdentifier = new ArrayList<Object>();
        }
        return this.itemIdentityOrSubjectLocatorOrSubjectIdentifier;
    }

    /**
     * Gets the value of the instanceOf property.
     * 
     * @return
     *     possible object is
     *     {@link InstanceOf }
     *     
     */
    public InstanceOf getInstanceOf() {
        return instanceOf;
    }

    /**
     * Sets the value of the instanceOf property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstanceOf }
     *     
     */
    public void setInstanceOf(InstanceOf value) {
        this.instanceOf = value;
    }

    /**
     * Gets the value of the nameOrOccurrence property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nameOrOccurrence property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNameOrOccurrence().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Name }
     * {@link Occurrence }
     * 
     * 
     */
    public List<Object> getNameOrOccurrence() {
        if (nameOrOccurrence == null) {
            nameOrOccurrence = new ArrayList<Object>();
        }
        return this.nameOrOccurrence;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}
package models.campaign;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import models.SuperModel;

@Entity
public class Perks extends SuperModel{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;
    
    /*
     * foreign key to campaign Id
     */
    public Long refId;

	/*
	 * type of campaign
	 */
    public String refType;
	
    /*
     * 1 - Gift certificate, 
     * 2- A cash reward - in an affiliate plan, 
     * 3- A free Trial
     * 4- Give away
     * 5- Discount code
     */
    public int perkTypeCode; 
    
    /*
     * number of units
     * e.g. 5 Gift certs, 20 give aways
     * May only apply to certain perkTypes
     */
    public int quantity;
    
    /*
     * Value of award if gift cert or cash reward
     * //$25, $300
     */
    public Long value;
    
    /*
     * to first 20 participants
     * or share among all participants based on collective outreach generated in each 30 day period 
     */
    public String criteria;
    
    /*
     * the frequency with which the perk is renewed within the perk active period
     * 1- weekly
     * 2- bi-weekly
     * 3-monthly
     * 4-every 3 months
     */
    public int recurringFrequency;
    
    
    /*
     * Perk begin date
     */
    @Column(name = "begin_date")
	public Date beginDate;

    /*
     * perk end date
     */
	@Column(name = "end_date")
	public Date endDate;

	
}

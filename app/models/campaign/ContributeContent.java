package models.campaign;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ContributeContent extends SuperCampaign{
	
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

}

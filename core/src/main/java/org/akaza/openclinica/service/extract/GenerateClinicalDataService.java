package org.akaza.openclinica.service.extract;

import java.util.ArrayList;
import java.util.List;

import org.akaza.openclinica.bean.extract.odm.FullReportBean;
import org.akaza.openclinica.bean.odmbeans.OdmClinicalDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ExportFormDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ExportStudyEventDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ExportSubjectDataBean;
import org.akaza.openclinica.bean.submit.crfdata.FormDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemGroupDataBean;
import org.akaza.openclinica.dao.hibernate.StudyDao;
import org.akaza.openclinica.dao.hibernate.StudySubjectDao;
import org.akaza.openclinica.domain.datamap.EventCrf;
import org.akaza.openclinica.domain.datamap.Item;
import org.akaza.openclinica.domain.datamap.ItemData;
import org.akaza.openclinica.domain.datamap.ItemGroupMetadata;
import org.akaza.openclinica.domain.datamap.Study;
import org.akaza.openclinica.domain.datamap.StudyEvent;
import org.akaza.openclinica.domain.datamap.StudySubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * To generate CDISC-ODM clinical data without data set.
 * @author jnyayapathi
 *
 */

public class GenerateClinicalDataService {
	 protected final static Logger logger = LoggerFactory.getLogger("org.akaza.openclinica.service.extract.GenerateClinicalDataService");
	 
	 private StudyDao studyDao;
	 
	 private StudySubjectDao studySubjectDao;
	 
	 public StudySubjectDao getStudySubjectDao() {
		return studySubjectDao;
	}
	public void setStudySubjectDao(StudySubjectDao studySubjectDao) {
		this.studySubjectDao = studySubjectDao;
	}
	public GenerateClinicalDataService(){
		 
	 }
	 public GenerateClinicalDataService(String StudyOID){
		 
		 
		 
		 
	 }
	 
	 public void getClinicalData(String studyOID){
		 Study study = new Study();
		 study.setOc_oid(studyOID);
		study =  getStudyDao().findByColumnName(studyOID, "oc_oid");
		
		 //  Study  study=  getStudyDaoHib().findById(studyBean.getId());
	        //Study study = getStudyDaoHib().findByColumnName(studyOID, "oc_oid");
	       //System.out.println("Study name"+study.getStudyId());
	      // System.out.println(study.getStudies().get(0).getStudy().getEventDefinitionCrfs().get(0).getCrfVersion().getName());
		 
	 }
	 
	 public String getClinicalData(String studyOID,String studySubjectOID){
		 Study study = getStudyDao().findByColumnName(studyOID,"oc_oid");
		 StudySubject studySubj = getStudySubjectDao().findByColumnName(studySubjectOID,"ocOid");
		return constructClinicalData(study,studySubj);
		// return null;
	 }
	public StudyDao getStudyDao() {
		return studyDao;
	}
	public void setStudyDao(StudyDao studyDao) {
		this.studyDao = studyDao;
	}
	
	private String constructClinicalData(Study study,StudySubject studySubj){
		
		
		return constructClinicalDataStudy(studySubj);
	}
	
	private String constructClinicalDataStudy(StudySubject studySubj){
		OdmClinicalDataBean odmClinicalDataBean = new OdmClinicalDataBean();
		
		ExportSubjectDataBean expSubjectBean = setExportSubjectDataBean(studySubj);
		
		List<ExportSubjectDataBean> exportSubjDataBeanList = new ArrayList<ExportSubjectDataBean>(); 
		exportSubjDataBeanList.add(expSubjectBean);
		odmClinicalDataBean.setExportSubjectData(exportSubjDataBeanList);
		odmClinicalDataBean.setStudyOID(studySubj.getStudy().getOc_oid());
		
		
		 FullReportBean report = new FullReportBean();
         report.setClinicalData(odmClinicalDataBean);
         report.createChunkedOdmXml(Boolean.TRUE, true, true);
        return report.getXmlOutput().toString();
		//return null;
	}
	
	private ExportSubjectDataBean setExportSubjectDataBean(StudySubject studySubj){
		
		
		ExportSubjectDataBean exportSubjectDataBean = new ExportSubjectDataBean();
		//exportSubjectDataBean.setAuditLogs(studySubj.getA)
		exportSubjectDataBean.setDateOfBirth(studySubj.getSubject().getDateOfBirth()+"");
		exportSubjectDataBean.setStudySubjectId(studySubj.getStudySubjectId()+"");
		exportSubjectDataBean.setSecondaryId(studySubj.getSecondaryLabel());
		exportSubjectDataBean.setStatus(studySubj.getStatus().toString());
		
		exportSubjectDataBean.setExportStudyEventData(setExportStudyEventDataBean(studySubj));
		
		exportSubjectDataBean.setSubjectOID(studySubj.getOcOid());
	//	exportSubjectDataBean.setStudyEventData(studyEventData)
		//exportSubjectDataBean.setDiscrepancyNotes(studySubj.getSubject().getDnStudySubjectMaps());
		return exportSubjectDataBean;
		
			
	}
	
	private ArrayList<ExportStudyEventDataBean> setExportStudyEventDataBean(StudySubject ss){
		ArrayList<ExportStudyEventDataBean> al = new ArrayList<ExportStudyEventDataBean>();
		
		for(StudyEvent se : ss.getStudyEvents())
		{
			ExportStudyEventDataBean expSEBean = new ExportStudyEventDataBean();
			expSEBean.setLocation(se.getLocation());
			expSEBean.setEndDate(se.getDateEnd()+"");
			expSEBean.setStartDate(expSEBean.getStartDate()+"");
			expSEBean.setStudyEventOID(se.getStudyEventDefinition().getOcOid());
			expSEBean.setStudyEventRepeatKey(se.getStudyEventDefinition().getOrdinal().toString());
			expSEBean.setExportFormData(getFormDataForClinicalStudy(se));
			
			al.add(expSEBean);
		}
		
		return al;
	}
	
	private ArrayList<ExportFormDataBean> getFormDataForClinicalStudy(StudyEvent se) {
		List<ExportFormDataBean> formDataBean = new ArrayList<ExportFormDataBean>();
		for(EventCrf ecrf:se.getEventCrfs()){
			ExportFormDataBean dataBean = new ExportFormDataBean();
			//dataBean.setDiscrepancyNotes(ecrf)
			//dataBean.setItemGroupData(getItemData(ecrf));
			dataBean.setItemGroupData(getItemData(ecrf.getCrfVersion().getItemGroupMetadatas()));
			dataBean.setFormOID(ecrf.getCrfVersion().getOcOid());
			dataBean.setInterviewDate(ecrf.getDateInterviewed()+"");
			dataBean.setInterviewerName(ecrf.getInterviewerName());
			dataBean.setStatus(ecrf.getStatus()+"");
			formDataBean.add(dataBean);
			
		}
		return (ArrayList<ExportFormDataBean>) formDataBean;
	}
	
	
	private ArrayList<ImportItemGroupDataBean> getItemData(
			List<ItemGroupMetadata> itemGroupMetadatas) {
		ArrayList<ImportItemGroupDataBean> iigDataBean =new ArrayList<ImportItemGroupDataBean>();
		for(ItemGroupMetadata igMetadata:itemGroupMetadatas){
			ImportItemGroupDataBean importIDBean = new ImportItemGroupDataBean();
			importIDBean.setItemGroupOID(igMetadata.getItemGroup().getOcOid());
			setItemDataValues(importIDBean,igMetadata.getItem());
			iigDataBean.add(importIDBean);
		}
		return iigDataBean;
	}
	private void setItemDataValues(ImportItemGroupDataBean importIDBean,
			Item item) {
		
		for(ItemData id :item.getItemDatas()){
			ImportItemDataBean iiDataBean = new ImportItemDataBean();
			iiDataBean.setValue(id.getValue());
			iiDataBean.setItemOID(id.getItem().getOcOid());
			importIDBean.getItemData().add(iiDataBean);
		}
		
	}
//	private ArrayList<ImportItemGroupDataBean> getItemData(EventCrf ecrf) {
//		ArrayList<ImportItemGroupDataBean> iigDataBean =new ArrayList<ImportItemGroupDataBean>();
//		for(ItemData itemData:ecrf.getItemDatas()){
//			ImportItemGroupDataBean importGroupData = new ImportItemGroupDataBean();
//			
//			importGroupData.setItemGroupOID(itemData.getItem().getItemGroupMetadatas().)
//			itemData.get
//		}
//		
//		return null;
//	}
//	
	
}
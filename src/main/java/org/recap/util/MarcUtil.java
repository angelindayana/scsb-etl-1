package org.recap.util;

import org.apache.commons.lang3.StringUtils;
import org.recap.model.jaxb.marc.ControlFieldType;
import org.recap.model.jaxb.marc.DataFieldType;
import org.recap.model.jaxb.marc.RecordType;
import org.recap.model.jaxb.marc.SubfieldatafieldType;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by pvsubrah on 6/15/16.
 */
public class MarcUtil {

    public String getDataFieldValue(RecordType marcRecord, String field, String ind1, String ind2, String subField) {
        List<String> strings = resolveValue(marcRecord, field, ind1, ind2, subField);
        return CollectionUtils.isEmpty(strings) ? "" : strings.get(0);
    }

    public List<String> getMultiDataFieldValues(RecordType marcRecord, String field, String ind1, String ind2, String subField) {
        return resolveValue(marcRecord, field, ind1, ind2, subField);
    }

    private List<String> resolveValue(RecordType marcRecord, String field, String ind1, String ind2, String subField) {
        List<String> values = new ArrayList<>();
        String indicator1 = (StringUtils.isNotBlank(ind1) ? String.valueOf(ind1.charAt(0)) : " ");
        String indicator2 = (StringUtils.isNotBlank(ind2) ? String.valueOf(ind2.charAt(0)) : " ");
        List<DataFieldType> dataFields = marcRecord.getDatafield();

        for (Iterator<DataFieldType> dataFieldIterator = dataFields.iterator(); dataFieldIterator.hasNext(); ) {
            DataFieldType dataField = dataFieldIterator.next();
            if (dataField != null && dataField.getTag().equals(field)) {
                if (doIndicatorsMatch(indicator1, indicator2, dataField)) {
                    List<SubfieldatafieldType> subFields = dataField.getSubfield();
                    for (Iterator<SubfieldatafieldType> subfieldIterator = subFields.iterator(); subfieldIterator.hasNext(); ) {
                        SubfieldatafieldType subfieldatafieldType = subfieldIterator.next();
                        if (subField != null && subfieldatafieldType.getCode().equals(subField)) {
                            String data = subfieldatafieldType.getCode();
                            if (StringUtils.isNotBlank(data)) {
                                values.add(subfieldatafieldType.getValue());
                            }
                        }
                    }
                }
            }
        }
        return values;
    }

    public String getInd1(RecordType marcRecord, String field, String subField) {
        List<DataFieldType> dataFields = marcRecord.getDatafield();

        for (Iterator<DataFieldType> dataFieldIterator = dataFields.iterator(); dataFieldIterator.hasNext(); ) {
            DataFieldType dataField = dataFieldIterator.next();
            if (dataField != null && dataField.getTag().equals(field)) {
                List<SubfieldatafieldType> subFields = dataField.getSubfield();
                for (Iterator<SubfieldatafieldType> subfieldIterator = subFields.iterator(); subfieldIterator.hasNext(); ) {
                    SubfieldatafieldType subfieldatafieldType = subfieldIterator.next();
                    if (subField != null && subfieldatafieldType.getCode().equals(subField)) {
                        return dataField.getInd1();
                    }
                }
            }
        }
        return null;
    }

    private boolean doIndicatorsMatch(String indicator1, String indicator2, DataFieldType dataField) {
        boolean result = true;
        if (StringUtils.isNotBlank(indicator1)) {
            result = dataField.getInd1().equals(indicator1.charAt(0));
        }
        if (StringUtils.isNotBlank(indicator2)) {
            result &= dataField.getInd2().equals(indicator2.charAt(0));
        }
        return result;
    }

    public String getControlFieldValue(RecordType marcRecord, String field) {
        List<ControlFieldType> controlFields = marcRecord.getControlfield();
        for (Iterator<ControlFieldType> variableFieldIterator = controlFields.iterator(); variableFieldIterator.hasNext(); ) {
            ControlFieldType controlField = variableFieldIterator.next();
            if (controlField != null && controlField.getTag().equals(field)) {
                return controlField.getValue();
            }
        }
        return null;
    }

    public boolean isSubFieldExists(RecordType marcRecord, String field) {
        List<DataFieldType> dataFields = marcRecord.getDatafield();
        for (Iterator<DataFieldType> dataFieldIterator = dataFields.iterator(); dataFieldIterator.hasNext(); ) {
            DataFieldType dataField = dataFieldIterator.next();
            if (dataField != null && dataField.getTag().equals(field)) {
                List<SubfieldatafieldType> subFields = dataField.getSubfield();
                for (Iterator<SubfieldatafieldType> subfieldIterator = subFields.iterator(); subfieldIterator.hasNext(); ) {
                    SubfieldatafieldType subfieldatafieldType = subfieldIterator.next();
                    String data = subfieldatafieldType.getCode();
                    if (StringUtils.isNotBlank(data)) {
                        String value = subfieldatafieldType.getValue();
                        if (StringUtils.isNotBlank(value)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        }
        return false;
    }

}

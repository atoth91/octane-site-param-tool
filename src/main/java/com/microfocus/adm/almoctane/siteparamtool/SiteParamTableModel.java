package com.microfocus.adm.almoctane.siteparamtool;

import java.util.*;
import java.util.stream.Collectors;

import javax.swing.table.AbstractTableModel;

public class SiteParamTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private List<SiteParam> siteParams = new ArrayList<SiteParam>();
    private List<SiteParam> filteredSiteParams = new ArrayList<SiteParam>();
    private String filter;

    public SiteParamTableModel() {
    }

    public void setFilter(String filter) {
        this.filter = filter;
        calcFilteredParams();
    }

    public SiteParamTableModel(List<SiteParam> siteParams) {
        this.siteParams = siteParams;
        calcFilteredParams();
    }

    public void addAll(Collection<? extends SiteParam> c) {
        siteParams.addAll(c);
        calcFilteredParams();
    }

    public void add(SiteParam e) {
        siteParams.add(e);
        calcFilteredParams();
    }

    public void clear() {
        siteParams.clear();
        calcFilteredParams();
    }

    public void setSiteParams(List<SiteParam> siteParams) {
        this.siteParams = siteParams;
        calcFilteredParams();
    }

    public List<SiteParam> getDirtySiteParams() {
        return siteParams.stream().filter(SiteParam::isDirty).collect(Collectors.toList());
    }

    private void calcFilteredParams() {
        if (filter != null && !filter.isEmpty()) {
            filteredSiteParams.clear();
            siteParams.forEach(siteParam -> {
                if (stringIsLikeQuery(siteParam.toString(), filter)) {
                    filteredSiteParams.add(siteParam);
                }
            });
        } else {
            filteredSiteParams.clear();
            filteredSiteParams.addAll(siteParams);
        }

        fireTableRowsUpdated(0, filteredSiteParams.size());
        fireTableDataChanged();
    }

    private boolean stringIsLikeQuery(String string, String query) {
        String sanitizedString = string.toLowerCase().trim();
        String sanitizedQuery = query.toLowerCase().trim();
        return sanitizedString.contains(sanitizedQuery) || sanitizedQuery.contains(sanitizedString);
    }

    @Override
    public int getRowCount() {
        return filteredSiteParams.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        calcFilteredParams();
        SiteParam siteParam = filteredSiteParams.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return siteParam.getParamName();
            case 1:
                return siteParam.getParamValue();
            case 2:
                return siteParam.isDirty();
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Param ID";
            case 1:
                return "Param Value";
            case 2:
                return "Modified";
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == 1) {
            SiteParam siteParam = filteredSiteParams.get(row);
            String val = value == null ? null : value.toString();
            if (!Objects.equals(siteParam.getParamValue(), val)) {
                siteParam.setParamValue(val);
                siteParam.setDirty(true);
                fireTableCellUpdated(row, col);
                fireTableDataChanged();
            }
        }
    }


}
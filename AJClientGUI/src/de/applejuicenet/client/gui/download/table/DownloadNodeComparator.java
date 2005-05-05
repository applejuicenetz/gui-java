package de.applejuicenet.client.gui.download.table;

import java.util.Comparator;

import de.applejuicenet.client.shared.util.DownloadCalculator;


public class DownloadNodeComparator implements Comparator{

    public static enum SORT_TYPE {SORT_NO_SORT, SORT_DOWNLOADNAME, SORT_GROESSE, 
        SORT_BEREITS_GELADEN, SORT_RESTZEIT, SORT_PROZENT, SORT_PWDL, 
        SORT_REST_ZU_LADEN, SORT_GESCHWINDIGKEIT, SORT_STATUS
    }
    
    private boolean isAscending = true;
    private SORT_TYPE sort = SORT_TYPE.SORT_DOWNLOADNAME;
    
    public DownloadNodeComparator(){}
    
    void setSortCriteria(SORT_TYPE sortType, boolean ascending){
        this.sort = sortType;
        isAscending = ascending;
    }
    
    public int compare(Object o1, Object o2) {
        int result;
        if (o1.getClass() == DownloadNode.class
            && o2.getClass() == DownloadMainNode.class) {
                return -1;
        }
        else if (o1.getClass() == DownloadMainNode.class
                 && o2.getClass() == DownloadNode.class) {
            return 1;
        }
        else if (o1.getClass() == DownloadNode.class
                 && o2.getClass() == DownloadNode.class) {
            result = ( (DownloadNode) o1).getPath().            
                compareToIgnoreCase( ( (DownloadNode) o2).
                                    getPath());
            return isAscending ? result : result*-1;
        }
        else{
            if (sort == SORT_TYPE.SORT_DOWNLOADNAME) {
                o1 = ( (DownloadMainNode) o1).getDownload().
                    getFilename();
                o2 = ( (DownloadMainNode) o2).getDownload().
                    getFilename();
            }
            else if (sort == SORT_TYPE.SORT_GROESSE) {
                o1 = new Long( ( (DownloadMainNode) o1).getDownload().
                              getGroesse());
                o2 = new Long( ( (DownloadMainNode) o2).getDownload().
                              getGroesse());
            }
            else if (sort == SORT_TYPE.SORT_BEREITS_GELADEN) {
                o1 = new Long( ( (DownloadMainNode) o1).getDownload().
                              getBereitsGeladen());
                o2 = new Long( ( (DownloadMainNode) o2).getDownload().
                              getBereitsGeladen());
            }
            else if (sort == SORT_TYPE.SORT_RESTZEIT) {
                o1 = new Long( ( (DownloadMainNode) o1).getDownload().
                              getRestZeit());
                o2 = new Long( ( (DownloadMainNode) o2).getDownload().
                              getRestZeit());
            }
            else if (sort == SORT_TYPE.SORT_PROZENT) {
                o1 = new Double( ( (DownloadMainNode) o1).
                                getDownload().getProzentGeladen());
                o2 = new Double( ( (DownloadMainNode) o2).
                                getDownload().getProzentGeladen());
            }
            else if (sort == SORT_TYPE.SORT_PWDL) {
                o1 = new Integer( ( (DownloadMainNode) o1).
                                 getDownload().getPowerDownload());
                o2 = new Integer( ( (DownloadMainNode) o2).
                                 getDownload().getPowerDownload());
            }
            else if (sort == SORT_TYPE.SORT_REST_ZU_LADEN) {
                o1 = new Long( ( (DownloadMainNode) o1).getDownload().
                              getGroesse() -
                              ( (DownloadMainNode) o1).getDownload().
                              getBereitsGeladen());
                o2 = new Long( ( (DownloadMainNode) o2).getDownload().
                              getGroesse() -
                              ( (DownloadMainNode) o2).getDownload().
                              getBereitsGeladen());
            }
            else if (sort == SORT_TYPE.SORT_GESCHWINDIGKEIT) {
                o1 = new Long( ( (DownloadMainNode) o1).getDownload().
                              getSpeedInBytes());
                o2 = new Long( ( (DownloadMainNode) o2).getDownload().
                              getSpeedInBytes());
            }
            else if (sort == SORT_TYPE.SORT_STATUS) {
                o1 = ( DownloadCalculator.getStatusAsString(((DownloadMainNode)
                        o1).getDownload()));
                o2 = ( DownloadCalculator.getStatusAsString(((DownloadMainNode)
                        o2).getDownload()));
            }
    
            if (o1 == null && o2 == null) {
                result = 0;
            }
            else if (o1 == null) {
                result = -1;
            }
            else if (o2 == null) {
                result = 1;
            }
            else {
                if (o1.getClass().getSuperclass() == Number.class) {
                    result = compare( (Number) o1, (Number) o2);
                }
                else if (o1.getClass() == Boolean.class) {
                    result = compare( (Boolean) o1, (Boolean) o2);
                }
                else {
                    result = ( (String) o1).compareToIgnoreCase( (String) o2);
                }
            }
        }
        return isAscending ? result : result*-1;
    }

    public int compare(Number o1, Number o2) {
        double n1 = o1.doubleValue();
        double n2 = o2.doubleValue();
        if (n1 < n2) {
            return -1;
        }
        else if (n1 > n2) {
            return 1;
        }
        else {
            return 0;
        }
    }

    public int compare(Boolean o1, Boolean o2) {
        boolean b1 = o1.booleanValue();
        boolean b2 = o2.booleanValue();
        if (b1 == b2) {
            return 0;
        }
        else if (b1) {
            return 1;
        }
        else {
            return -1;
        }
    }


}

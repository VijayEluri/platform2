package com.idega.util;

import java.util.GregorianCalendar;
import com.idega.jmodule.object.*;
import java.util.Calendar;
import java.sql.*;
import java.util.Locale;



public class idegaCalendar{

    private boolean bIsHoliday = false;
    private String temp_holiday_name = "";
    private int[] easterDay = {0,0,0};

        public boolean isFullMoon(int ar, int manudur,int dagur) {
            boolean returner = false;
            if (getMoonStatus(ar,manudur,dagur) == 1) {
                returner = true;
            }
            return false;
        }

        public boolean isFullMoon(GregorianCalendar calendar) {
            boolean returner = false;
            if (getMoonStatus(calendar.YEAR,calendar.MONTH +1,calendar.DAY_OF_MONTH) == 0) {
                returner = true;
            }
            return false;
        }

        public int[] getNextFullMoon(int ar, int manudur, int dagur) {
            int return_year = 0;
            int return_month = 0;
            int return_day = 0;
            int temp_year = ar;
            int temp_month = manudur;
            int temp_day = dagur;

            boolean done = false;
            int teljari = 0;


            while ((!done) && (teljari < 100)) {
                ++teljari;

//                correctDate(temp_year,temp_month,temp_day);

                if (temp_day == getLengthOfMonth(temp_month,temp_year)+1) {
                    ++temp_month;
                    if (temp_month == 13) {
                      ++temp_year;
                      temp_month = 1;
                    }
                    temp_day = 1;
                }

                if (getMoonStatus(temp_year,temp_month,temp_day) == 1 ) {
                    return_month = temp_month;
                    return_day = temp_day;
                    return_year = temp_year;
                    done = true;
                }

                temp_day++;
            }
            int[] returner = {return_year,return_month,return_day};
            System.out.println("N�sta fulla tungl : "+return_year+" "+return_month+" "+return_day);

            return returner;

        }

        public int[] getLastFullMoon(int ar, int manudur, int dagur) {
            int return_year = 0;
            int return_month = 0;
            int return_day = 0;
            int temp_year = ar;
            int temp_month = manudur;
            int temp_day = dagur;

            boolean done = false;
            int teljari = 0;


            while ((!done) && (teljari < 100)) {
                ++teljari;

                correctDate(temp_year,temp_month,temp_day);
/*
                if (temp_day == 0) {
                    --temp_month;
                    if (temp_month == 0) {
                      --temp_year;
                      temp_month = 12;
                    }
                    temp_day = this.getLengthOfMonth(temp_month,temp_year);
                }
*/
                if (getMoonStatus(temp_year,temp_month,temp_day) == 1 ) {
                    return_month = temp_month;
                    return_day = temp_day;
                    return_year = temp_year;
                    done = true;
                }

                temp_day--;
            }
            int[] returner = {return_year,return_month,return_day};
            System.out.println(return_year+" "+return_month+" "+return_day);

            return returner;


        }

        public int getMoonStatus(int ar, int manudur,int dagur) {
            int returner = -1;

            if (ar == 2001) {
                if ((manudur == 3) && (dagur==9)) {
                    returner = 1;
                }
                else if ((manudur == 4) && (dagur==7)) {
                    returner = 1;
                }
                else if ((manudur == 5) && (dagur==7)) {
                    returner = 1;
                }
                else if ((manudur == 6) && (dagur==5)) {
                    returner = 1;
                }
                else if ((manudur == 7) && (dagur==5)) {
                    returner = 1;
                }
                else if ((manudur == 8) && (dagur==3)) {
                    returner = 1;
                }
                else if ((manudur == 9) && (dagur==2)) {
                    returner = 1;
                }
                else if ((manudur == 10) && (dagur==1)) {
                    returner = 1;
                }
                else if ((manudur == 10) && (dagur==30)) {
                    returner = 1;
                }
                else if ((manudur == 11) && (dagur==29)) {
                    returner = 1;
                }
                else if ((manudur == 12) && (dagur==28)) {
                    returner = 1;
                }
            }
            else if (ar==2002) {
                if ((manudur == 1) && (dagur==27)) {
                    returner = 1;
                }
                else if ((manudur == 2) && (dagur==26)) {
                    returner = 1;
                }
                else if ((manudur == 3) && (dagur==27)) {
                    returner = 1;
                }
                else if ((manudur == 4) && (dagur==26)) {
                    returner = 1;
                }
                else if ((manudur == 5) && (dagur==25)) {
                    returner = 1;
                }
                else if ((manudur == 6) && (dagur==24)) {
                    returner = 1;
                }
                else if ((manudur == 7) && (dagur==23)) {
                    returner = 1;
                }
                else if ((manudur == 8) && (dagur==22)) {
                    returner = 1;
                }
                else if ((manudur == 9) && (dagur==20)) {
                    returner = 1;
                }
                else if ((manudur == 10) && (dagur==20)) {
                    returner = 1;
                }
                else if ((manudur == 11) && (dagur==18)) {
                    returner = 1;
                }
                else if ((manudur == 12) && (dagur==18)) {
                    returner = 1;
                }
            }
            else if (ar==2003) {
                if ((manudur == 1) && (dagur==16)) {
                    returner = 1;
                }
            }





            return returner;
        }

	public int getLengthOfMonth(String mon,String ar) {
            try {
		return getLengthOfMonth(Integer.parseInt(mon),Integer.parseInt(ar));
            }
            catch (NumberFormatException n) {
                return 0;
            }
	}
	public int getLengthOfMonth(String mon,int ar) {
            try {
		return getLengthOfMonth(Integer.parseInt(mon),ar);
            }
            catch (NumberFormatException n) {
                return 0;
            }
	}
	public int getLengthOfMonth(int mon,String ar) {
            try {
		return getLengthOfMonth(mon,Integer.parseInt(ar));
            }
            catch (NumberFormatException n) {
                return 0;
            }
	}


	public int getLengthOfMonth(int mon, int ar){
	    GregorianCalendar calendar = new GregorianCalendar();


	    int dagarman = 31;

		switch (mon) {
			case  0 :
		          dagarman = 31;
		          break;
                        case 1 :
                          dagarman=31;
                          break;
			case 2 :
      			        if (calendar.isLeapYear(ar)) {
                                  dagarman=29;
                                }
                                else {
                                  dagarman=28;
				}
                              break;
                        case 3 :
                          dagarman=31;
                          break;
			case 4 :
	                  dagarman = 30;
			  break;
                        case 5 :
                          dagarman=31;
                          break;
                        case 6 :
                          dagarman=30;
                          break;
                        case 7 :
                          dagarman=31;
                          break;
                        case 8 :
                          dagarman=31;
                          break;
                        case 9 :
                          dagarman=30;
                          break;
                        case 10 :
                          dagarman=31;
                          break;
                        case 11 :
                          dagarman=30;
                          break;
                        case 12 :
                          dagarman=31;
                          break;
                        case 13 :
                          dagarman=31;
                          break;

		}
		return dagarman;
	}

	public String getNameOfMonth(String manudur){
		return getNameOfMonth(Integer.parseInt(manudur));
	}

        public String getISLNameOfMonth(int month) {
            return getNameOfMonth(month);
        }

	public String getNameOfMonth(int mon){
    	  String manudurnafn ="";
          switch (mon) {
                  case 0  :
                                  manudurnafn=("Desember");
                          break;
                  case 01 :
                                  manudurnafn=("Jan�ar");
                          break;
                  case 02 :
                                  manudurnafn=("Febr�ar");
                          break;
                  case 03 :
                                  manudurnafn=("Mars");
                          break;
                  case 04 :
                                  manudurnafn=("Apr�l");
                          break;
                  case 05 :
                                  manudurnafn=("Ma�");
                          break;
                  case 06 :
                                  manudurnafn=("J�n�");
                          break;
                  case 07 :
                                  manudurnafn=("J�l�");
                          break;
                  case 8 :
                                  manudurnafn=("�g�st");
                          break;
                  case 9 :
                                  manudurnafn=("September");
                          break;
                  case 10 :
                                  manudurnafn=("Okt�ber");
                          break;
                  case 11 :
                                  manudurnafn=("N�vember");
                          break;
                  case 12 :
                                  manudurnafn=("Desember");
                          break;
                  case 13 :
                                  manudurnafn=("Jan�ar");
                          break;
            }
            return manudurnafn;
	}

	public String getNameOfMonth(int month, ModuleInfo modinfo){
            Locale currentLocale = modinfo.getCurrentLocale();
            String returner = "";

            if(currentLocale.equals(com.idega.util.LocaleUtil.getIcelandicLocale())){
                returner = getISLNameOfMonth(month);
            }else if (currentLocale.equals(Locale.ENGLISH)){
                returner = getENGNameOfMonth(month);
            }else if (currentLocale.equals(Locale.UK)){
                returner = getENGNameOfMonth(month);
            }else if (currentLocale.equals(Locale.US)){
                returner = getENGNameOfMonth(month);
            }else {
                returner = getENGNameOfMonth(month);
            }

            return returner;
        }


        public String getENGNameOfMonth(int month) {
            String manudurnafn = "";
		switch (month) {
			case 0  :
					manudurnafn=("December");
				break;
			case 01 :
					manudurnafn=("Janary");
				break;
			case 02 :
					manudurnafn=("February");
				break;
			case 03 :
					manudurnafn=("March");
				break;
			case 04 :
					manudurnafn=("April");
				break;
			case 05 :
					manudurnafn=("May");
				break;
			case 06 :
					manudurnafn=("June");
				break;
			case 07 :
					manudurnafn=("July");
				break;
			case 8 :
					manudurnafn=("August");
				break;
			case 9 :
					manudurnafn=("September");
				break;
			case 10 :
					manudurnafn=("October");
				break;
			case 11 :
					manudurnafn=("November");
				break;
			case 12 :
					manudurnafn=("December");
				break;
			case 13 :
					manudurnafn=("January");
				break;
		}
         	return manudurnafn;
	}


	public int getWeekOfYear(int ar, int manudur, int dagur) {

		GregorianCalendar calendar = new GregorianCalendar(ar,manudur-1,dagur);

		int vdagur = calendar.get(calendar.WEEK_OF_YEAR) +2;


                return vdagur;
	}


	public String getShortNameOfMonth(int mon){
	String manudurnafn ="";

		switch (mon) {
			case 0  :
					manudurnafn=("des");
				break;
			case 01 :
					manudurnafn=("jan");
				break;
			case 02 :
					manudurnafn=("feb");
				break;
			case 03 :
					manudurnafn=("mars");
				break;
			case 04 :
					manudurnafn=("apr�l");
				break;
			case 05 :
					manudurnafn=("ma�");
				break;
			case 06 :
					manudurnafn=("j�n�");
				break;
			case 07 :
					manudurnafn=("j�l�");
				break;
			case 8 :
					manudurnafn=("�g�st");
				break;
			case 9 :
					manudurnafn=("sept");
				break;
			case 10 :
					manudurnafn=("okt");
				break;
			case 11 :
					manudurnafn=("n�v");
				break;
			case 12 :
					manudurnafn=("des");
				break;
			case 13 :
					manudurnafn=("jan");
				break;
		}
		return manudurnafn;
	}

	public String getShortNameOfMonth(int month, ModuleInfo modinfo){
            Locale currentLocale = modinfo.getCurrentLocale();
            String returner = "";

            if(currentLocale.equals(com.idega.util.LocaleUtil.getIcelandicLocale())){
                returner = getShortISLNameOfMonth(month);
            }else if (currentLocale.equals(Locale.ENGLISH)){
                returner = getShortENGNameOfMonth(month);
            }else if (currentLocale.equals(Locale.UK)){
                returner = getShortENGNameOfMonth(month);
            }else if (currentLocale.equals(Locale.US)){
                returner = getShortENGNameOfMonth(month);
            }else {
                returner = getShortENGNameOfMonth(month);
            }

            return returner;


        }


        public String getShortISLNameOfMonth(int mon) {
            return getShortNameOfMonth(mon);
        }

        public String getShortENGNameOfMonth(int mon) {


            String manudurnafn ="";

            switch (mon) {
              case 0  :
                              manudurnafn=("Dec");
                      break;
              case 01 :
                              manudurnafn=("Jan");
                      break;
              case 02 :
                              manudurnafn=("Feb");
                      break;
              case 03 :
                              manudurnafn=("Mar");
                      break;
              case 04 :
                              manudurnafn=("Apr");
                      break;
              case 05 :
                              manudurnafn=("May");
                      break;
              case 06 :
                              manudurnafn=("Jun");
                      break;
              case 07 :
                              manudurnafn=("Jul");
                      break;
              case 8 :
                              manudurnafn=("Aug");
                      break;
              case 9 :
                              manudurnafn=("Sep");
                      break;
              case 10 :
                              manudurnafn=("Oct");
                      break;
              case 11 :
                              manudurnafn=("Nov");
                      break;
              case 12 :
                              manudurnafn=("Dec");
                      break;
              case 13 :
                              manudurnafn=("Jan");
                      break;
            }
            return manudurnafn;

	}


	public String getNameOfDay(String dagur) {
		return getNameOfDay(Integer.parseInt(dagur));
	}

	public String getNameOfDay(int dagur) {
		String nafn="";

		switch (dagur) {
			case 1:
				nafn=("Sunnudagur");
				break;
			case 2:
				nafn=("M�nudagur");
				break;
			case 3:
				nafn=("�ri�judagur");
				break;
			case 4:
				nafn=("Mi�vikudagur");
				break;
			case 5:
				nafn=("Fimmtudagur");
				break;
			case 6:
				nafn=("F�studagur");
				break;
			case 7:
				nafn=("Laugardagur");
				break;
		}
		return nafn;
	}


        public String getISLNameOfDay(int dagur) {
            return getNameOfDay(dagur);
        }

	public String getNameOfDay(int day, ModuleInfo modinfo) {
            Locale currentLocale = modinfo.getCurrentLocale();
            String returner = "";

            if(currentLocale.equals(com.idega.util.LocaleUtil.getIcelandicLocale())){
                returner = getISLNameOfDay(day);
            }else if (currentLocale.equals(Locale.ENGLISH)){
                returner = getENGNameOfDay(day);
            }else if (currentLocale.equals(Locale.UK)){
                returner = getENGNameOfDay(day);
            }else if (currentLocale.equals(Locale.US)){
                returner = getENGNameOfDay(day);
            }else {
                returner = getENGNameOfDay(day);
            }

            return returner;
        }

        public String getENGNameOfDay(int dagur) {
            String nafn= getNameOfDay(dagur);
            switch (dagur) {
                case 1:
                    nafn=("Sunday");
                    break;
                case 2:
                    nafn=("Monday");
                    break;
                case 3:
                    nafn=("Tuesday");
                    break;
                case 4:
                    nafn=("Wednesday");
                    break;
                case 5:
                    nafn=("Thursday");
                    break;
                case 6:
                    nafn=("Friday");
                    break;
                case 7:
                    nafn=("Saturday");
                    break;
            }
            return nafn;
	}


	public int getDayOfWeek(int ar,int manudur,int dagur) {

		GregorianCalendar calendar = new GregorianCalendar(ar,manudur-1,dagur);

		int vdagur = calendar.get(calendar.DAY_OF_WEEK);


                return vdagur;
	}

        /**
         * @Depricated
         */

	public boolean getHoliday(int ar, int manudur, int dagur) {
            return isHoliday(ar,manudur,dagur);
        }


        public String getSpecialNameOfDay(int ar, int manudur, int dagur) {
            String crapper = checkForHoliday(ar,manudur,dagur);
            String returner = "";
            if (crapper != null) {
                returner = crapper;
            }

            return returner;
        }



        public boolean isHoliday(int ar, int manudur, int dagur) {

            temp_holiday_name = checkForHoliday(ar,manudur,dagur);
            System.out.println("Fr�dagur "+ar+"/"+manudur+"/"+dagur+" heitir : "+temp_holiday_name);
            boolean returner = false;

            returner = bIsHoliday;

            bIsHoliday = false;

            return returner;
        }


        /**
         * Returns int[]
         * int[0] = year, int[1] = month, int[2] = day
         */

        public int[] getEasterDay(int year) {
            int a,b,c,d,e,f,g,h,i,j,k,m,n,p = 0;

            a = (int) year % 19;
            b = (int) year / 100;
            c = (int) year % 100;
            d = (int) b / 4;
            e = (int) b % 4;
            f = (int) (b+8) / 25;
            g = (int) (b-f+1) / 3;
            h = (int) ( (19*a) + b - d - g + 15 ) % 30;
            i = (int) c / 4;
            j = (int) c % 4;
            k = (int) ( 32 + (2*e) + (2*i) -h -j ) % 7;
            m = (int) ( a + (11*h) + (22*k) ) / 451;
            n = (int) ( h + k - (7*m) + 114 ) / 31;
            p = (int) ( h + k - (7*m) + 114 ) % 31;


            int[] returner = {year, n, (p+1)};

        return returner;

        /*
        Besta reiknireglan til a� finna p�ska � n�ja st�l mun vera s� sem birtist nafnlaust � breska t�maritinu Nature �ri� 1876. Reglan er �essi:

               Deili� � �rtali� me� 19 og kalli� afganginn a.
               Deili� � �rtali� me� 100, kalla� deildina b og afganginn c.
               Deili� � b me� 4, kalli� deildina d og afganginn e.
               Deili� � b + 8 me� 25 og kalli� deildina f.
               Deili� � b -f + 1 me� 3 og kalli� deildina g.
               Deili� � 19a + b - d - g + 15 me� 30 og kalli� afganginn h.
               Deili� � c me� 4 og kalli� deildina i og afganginn j.
               Deili� � 32 + 2e + 2i - h - j me� 7 og kalli� afganginn k.
               Deili� � a + 11h+22k me� 451 og kalli� deildina m.
               Deili� � h + k - 7m + 114 me� 31, kalli� deildina n og afganginn p.

        �� er n m�nu�urinn sem p�skadagur fellur �, og p + 1 er m�na�ardagurinn
        */


        }

        public void correctDate(int ar, int manudur, int dagur) {
            if (dagur < 1) {
                --manudur;
                if (manudur == 0) {
                  --ar;
                  manudur = 12;
                }
                dagur = getLengthOfMonth(manudur,ar) + dagur;
            }
            else if (dagur > getLengthOfMonth(manudur,ar)) {
                ++manudur;
                if (manudur == 13) {
                  ++ar;
                  manudur = 1;
                }
                dagur = dagur - getLengthOfMonth(manudur,ar);
            }

        }


        public String checkForHoliday(int ar, int manudur, int dagur) {
		boolean svara =false;
                String nameOfDay = null;

                int dayOfWeek = getDayOfWeek(ar,manudur,dagur);
                easterDay = getEasterDay(ar);
                idegaTimestamp stamp = new idegaTimestamp(easterDay[2],easterDay[1],easterDay[0]);





                        //  Finna Sumardaginn fyrsti
                        if (!(svara)) {
                            if ((manudur == 4) && (dagur >=19) && (dagur <= 25)) {
                                if (dayOfWeek == 5) {
                                  svara = true;
                                  nameOfDay= "Sumardagurinn fyrsti";
                                }
                            }
                        }

                        if (!(svara)) {
                            if ((manudur == 8) && (dagur >=1) && (dagur <= 7)) {
                                if (dayOfWeek == 2) {
                                  svara = true;
                                  nameOfDay= "Fr�dagur verslunarmanna";
                                }
                            }
                        }

			if (!(svara))
			switch (manudur) {
				case 1:
					if (dagur==1) {
                                            svara=true;
                                            nameOfDay= "N��rsdagur";
                                        }
					break;
				case 2:
					break;
				case 3:
                                        if (dagur == 20) {
                                            nameOfDay ="Vorjafnd�gur";
                                        }
					break;
				case 4:
					break;
				case 5:
					if (dagur==1) {
                                            svara=true;
                                            nameOfDay= "Verkal��sdagurinn";
                                        }
					break;
				case 6:
					if (dagur==17) {
                                            svara=true;
                                            nameOfDay= "L��veldisdagurinn";
                                        }
                                        else if (dagur == 21) {
                                            nameOfDay= "Sumars�lst��ur";
                                        }
					break;
				case 7:
					break;
				case 8:
					break;
				case 9:
					break;
				case 10:
                                        if (dagur==22) {
                                            nameOfDay= "Haustjafnd�gur";
                                        }
					break;
				case 11:
					break;
				case 12:
                                        if (dagur == 1) {
                                            nameOfDay = "Fullveldisdagurinn";
                                        }
                                        else if (dagur == 21) {
                                            nameOfDay ="Vetrars�lst��ur";
                                        }
                                        else if (dagur == 23) {
                                            nameOfDay ="�orl�ksmessa";
                                        }
					else if (dagur==24) {
                                            svara=true;  // eftir 12:00
                                            nameOfDay= "A�fangadagur";
                                        }
                                        else if (dagur==25) {
                                            svara=true;
                                            nameOfDay= "J�ladagur";
                                        }
					else if (dagur == 26) {
                                            svara=true;
                                            nameOfDay= "Annar � j�lum";
                                        }
                                        else if (dagur == 31) {
                                            svara = true;  // eftir 12:00
                                            nameOfDay= "Gaml�rsdagur";
                                        }
					break;
			}


                        //  Finna p�skadag
                        if (!(svara)) {
                            if ((ar == stamp.getYear()) && (manudur ==stamp.getMonth()) && (dagur == stamp.getDate()) ) {
                                nameOfDay= "P�skadagur";
                                svara = true;
                            }

                        }


                        // checka � d�gum eftir p�ska
                        if (!(svara)) {
                            stamp.addDays(1);
                            if ((ar == stamp.getYear()) && (manudur ==stamp.getMonth()) && (dagur == stamp.getDate()) ) {
                                nameOfDay= "Annar � p�skum";
                                svara = true;
                            }
                            if (!(svara)) {
                                stamp.addDays(38);
                                if ((ar == stamp.getYear()) && (manudur ==stamp.getMonth()) && (dagur == stamp.getDate()) ) {
                                    nameOfDay= "Uppstigningardagur";
                                    svara = true;
                                }
                                if (!(svara)) {
                                    stamp.addDays(10);
                                    if ((ar == stamp.getYear()) && (manudur ==stamp.getMonth()) && (dagur == stamp.getDate()) ) {
                                        nameOfDay= "Hv�tasunna";
                                        svara = true;
                                    }
                                    if (!(svara)) {
                                        stamp.addDays(1);
                                        if ((ar == stamp.getYear()) && (manudur ==stamp.getMonth()) && (dagur == stamp.getDate()) ) {
                                            nameOfDay= "Annar � hv�tasunnu";
                                            svara = true;
                                        }
                                    }
                                }
                            }
                        }
                        // stilla aftur � p�skadag
                        stamp.addDays(-50);


                        // checka � d�gum fyrir p�skadag
                        if (!(svara)) {
                            stamp.addDays(-1);
                            if ((ar == stamp.getYear()) && (manudur ==stamp.getMonth()) && (dagur == stamp.getDate()) ) {
                                nameOfDay="";
                                svara = true;
                            }
                            if (!(svara)) {
                                stamp.addDays(-1);
                                if ((ar == stamp.getYear()) && (manudur ==stamp.getMonth()) && (dagur == stamp.getDate()) ) {
                                    nameOfDay="F�studagurinn langi";
                                    svara = true;
                                }
                                if (!(svara)) {
                                    stamp.addDays(-1);
                                    if ((ar == stamp.getYear()) && (manudur ==stamp.getMonth()) && (dagur == stamp.getDate()) ) {
                                        nameOfDay="Sk�rdagur";
                                        svara = true;
                                    }
                                }
                            }
                        }

                        //  Laugadagur og sunnudagur
			if  (( dayOfWeek == 1) || (dayOfWeek == 7) ){
                            if (!svara) {
				svara = true;
                                nameOfDay="";
                            }
			}


                bIsHoliday = svara;
		return nameOfDay;
	}


	public String getDateStamp(){

		GregorianCalendar calendar = new GregorianCalendar();

		String stamp;

		int	dayM = calendar.get(calendar.DAY_OF_MONTH);
		int	dayW = calendar.get(calendar.DAY_OF_WEEK);
		int	monthY = calendar.get(calendar.MONTH)+1;
		int	year = calendar.get(calendar.YEAR);

		stamp=dayM+"."+getNameOfMonth(monthY)+" "+year;

	return stamp;

	}



/**
*
*Skila dagsetningu � mySQL formi
*
*
**/


	public String getDateStampRS(){

		GregorianCalendar calendar = new GregorianCalendar();

		String stamp;

		int	dayM = calendar.get(calendar.DAY_OF_MONTH);
		int	dayW = calendar.get(calendar.DAY_OF_WEEK);
		int	monthY = calendar.get(calendar.MONTH)+1;
		int	year = calendar.get(calendar.YEAR)+000;

		stamp=year+"-"+monthY+"-"+dayM;

	return stamp;

	}

	public int getMonth(){

		GregorianCalendar calendar = new GregorianCalendar();

		int month = calendar.get(calendar.MONTH)+1;

		return month;
	}

	public int getDay(){

		GregorianCalendar calendar = new GregorianCalendar();

		int day = calendar.get(calendar.DAY_OF_MONTH);

		return day;
	}

	public int getYear(){

		GregorianCalendar calendar = new GregorianCalendar();

		int year = calendar.get(calendar.YEAR);

		return year;
	}


	public int getRows(ResultSet RS){

		ResultSet RS_new= RS;
		int teljari=0;
		try {

			while (RS_new.next() ) {
				++teljari;
			}

			RS_new.close();
		}
		catch (SQLException E) {
    	E.printStackTrace();
    	}

	return teljari;
	}

	/**
	*Returns an new timestamp before (int) number of days in the past before timestamp_in
	**/
	public static Timestamp getTimestampBefore(Timestamp timestamp_in, int days){
		long milliseconds = timestamp_in.getTime();
		milliseconds = milliseconds - days*24*60*60*1000;
		return new Timestamp(milliseconds);
	}

	/**
	*Returns an new timestamp after (int) number of days in the future after timestamp_in
	**/
	public static Timestamp getTimestampAfter(Timestamp timestamp_in, int days){
		long milliseconds = timestamp_in.getTime();
		milliseconds = milliseconds + days*24*60*60*1000;
		return new Timestamp(milliseconds);
	}

	public String getTimeStamp(){

		String stamp;


		int	hours = GregorianCalendar.HOUR;
		int	minutes= GregorianCalendar.MINUTE;
		int seconds= GregorianCalendar.SECOND;

		stamp=hours+":"+minutes+":"+seconds;

	return stamp;



	}











}

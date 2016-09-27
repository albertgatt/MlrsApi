package test;

import junit.framework.TestCase;

public abstract class MLRSTests extends TestCase {
	protected String textWithURL = "Dan huwa url: http://www.google.com u dan ukoll: www.blabla.co.uk u dan: " 
			+ "http://electronics.cnet.com/electronics/0-6342366-8-8994967-1.html, kif ukoll dan: mailto:mlrs@gmail.com, imma dan mhuwhiex: http u dan lanqas: javascript:";
	
	protected String text1 = "Iż-żiemel mar jimxi u l-kelb ta' Bertu kielu, "
			+ "biċ-ċajt iċ-ċajt, imma diġa` kien sawwtu b'ċerta guffaġni "
			+ "li naturalment ma niżlitx tajjeb imma ovvjament dejqet lil ħafna nies.";
	protected String text2 = "Il-mara marret u r-raġel, li kien baqa' f'postu, miet.";
	protected String text3 = "Dan huwa simbolu: $1234";
	protected String text4 = "Xahar wara li l-gazzetta Illum żvelat li "
			+ "l-Awtorità Maltija dwar il-Komunikazzjoni (MCA) nediet "
			+ "investigazzjoni dwar il-ħlas ta’ ewro li Melita, il-kumpannija "
			+ "tat-telekomunikazzjonijiet, bdiet tiċċarġja għal kull pagament "
			+ "li jsir b'ċekk, jidher li issa l-kumpanija reġa’ bdielha u waqqfet "
			+ "dan il-ħlas. Mistoqsi dwar dan, kelliem għall-Melita ikkonferma ma’ "
			+ "Illum li l-ħlas kien twaqqaf sakemm tittieħed deċiżjoni finali dwar din il-materja.";

	protected String text5 = "Xahar wara li l-gazzetta Illum żvelat li "
			+ "l-Awtorità Maltija dwar il-Komunikazzjoni (MCA) nediet "
			+ "investigazzjoni dwar il-ħlas ta’ ewro li Melita, il-kumpannija "
			+ "tat-telekomunikazzjonijiet, bdiet tiċċarġja għal kull pagament "
			+ "li jsir b'ċekk, jidher li issa l-kumpanija reġa’ bdielha u waqqfet "
			+ "dan il-ħlas. \n\n"
			+ "Mistoqsi dwar dan, kelliem għall-Melita ikkonferma ma’ "
			+ "Illum li l-ħlas kien twaqqaf sakemm tittieħed deċiżjoni finali dwar din il-materja.";

	protected String phrase1 = "Fiż-żmien, kien hemm kumpanija żgħira li fetħet dan l-aħħar.";
	protected String phrase2 = "Safrattant, ir-raġel li kont iltqajt miegħu baqa' jinsisti li ma kellux x'jaqsam.";
	protected String phrase3 = "Il-Gvern Malti ħatar bħala mexxej tal-kumitat lil Ġwanni Mamo.";
	protected String phrase4 = "Il-Gvern Malti ħatar lil Ġwanni Mamo bħala mexxej tal-kumitati kollha ta' Sqallija.";
	protected String phrase5 = "Li ġara safrattant kien li r-raġel twil u oħxon telaq għal għonq it-triq.";
	protected String phrase6 = "Il-ħamis mort nixtri għand tal-kantuniera.";
	protected String phrase7 = "Fir-rebbiegħa bi ħsiebni nsib żwiemel tal-ostja.";
	protected String phrase8 = "B'ras ir-randan u bix-xita kollox sew!";

	protected String fakeMaltese1 = "Il Michael Angel Arch u I aka Michael għandhom tendenza li "
			+ "jkunu pjuttost somber u dritta sal-punt aħna qed jagħmlu "
			+ "fil-kitbiet passat tagħna, ma 'l-twitter okkażjonali ta' "
			+ "Humer imma jien nemmen li din Angel Arch u nixtieq li jiħfief il-paġni "
			+ "tagħna ta 'informazzjoni. Allura li kkommettiet Us li jiħfief il dan is-sit web, "
			+ "b'liema mod aħjar li tibda mbagħad mill-dritt tal-ħluq ta 'Babeş. Il-blog fuq ħsibijiet Jung, "
			+ "ippubblikat Mejju 2010 hawnhekk huwa ovvjament fil-qalba tas-suġġett tal-imħabba imma mbagħad - "
			+ "mill-ħluq ta 'Babeş";

	protected String fakeMaltese2 = "Il-fehma Console b'mod ċar jissepara output minn "
			+ "kull distinta proċess u jżommhom fid-diversi \"buffers\". "
			+ "Il-Console għandha mibnija fil-switch mixgħul karatteristika li awtomatikament "
			+ "jaqilbu l-għan li juru l-buffer ta 'l-aħħar proċess li jitwettaq l-output, "
			+ "madankollu tista' faċilment jaqleb il-wiri għal kull buffer proċess trid tħares lejn.";

	protected String english1 = "After the task is run, the index will be in the specified "
			+ "index directory in gzipped format, just like the output of the previous demos. "
			+ "For the 2009 MEDLINE release, that's a 25,443,818 byte gzipped file of counts at level 5_1. "
			+ "It may be read back into models, etc.";

	protected String english2 = "Reference will be made to a number of relevant studies and articles, "
			+ "both quantitative and qualitative, drawn from the students' areas of studies. "
			+ "Students will be guided in the analysis of the framework (Abstract, Introduction, Method "
			+ "and Materials, Results, Discussion) around which these texts are structured. "
			+ "Students will also be exercised in presenting their views in the form of a thesis "
			+ "statement predicated on a coherent and persuasive argument grounded in supporting evidence. "
			+ "Special attention will be paid to writer profile, i.e. the degree to which students are "
			+ "present in their writing in terms of voice, point of view and independent, critical thought.";

	protected String chinese1 = "我們是八方貿易—歡迎您:";

	protected String chinese2 = "(我們的產品包括~LV路易威登GUCCI香"
			+ "奈兒COACH包包．皮夾．NIKE愛迪達運動鞋．勞力士．歐美佳．萬寶龍等名錶．服裝．"
			+ "首飾配件．各山寨手機．dvd電影韓日劇) 應有盡有～我賣場沒有圖片的商品並不代表我沒有貨～"
			+ "品種太多無法全部刊登～您如果在別人賣場看到喜歡ㄉ商品～發圖片給我也可以訂到唷!";

	protected String[] spellCase1 = new String[] { "Hemmhekk", "il-",
			"familja", "setgÄ§et", "tonxor", "tagÄ§mel", "setgÄ§u",
			"jitilgÄ§u", "jilagÄ§bu" };

	protected String[] spellSolution1 = new String[] { "hemmhekk", "il-",
			"familja", "setgħet", "tonxor", "tagħmel", "setgħu", "jitilgħu",
			"jilagħbu" };

	protected String spellCase2 = "Minflok il-unjns inghaqdu ponn biex jiddefendu l-ħaddiem "
			+ "mill-kbir saz-zg?ir u jwiddbu lill-Gvern, "
			+ "tal-Gvern inghaqdu ponn, kesksu u firdu l-unjins bejniethom, u hattew l-MUT!";

	protected String spellSolution2 = "Minflok il-unjins ingħaqdu ponn biex jiddefendu l-ħaddiem "
			+ "mill-kbir saz-zgħir u jwiddbu lill-Gvern, tal-Gvern "
			+ "ingħaqdu ponn, kesksu u firdu l-unjins bejniethom, u ħattew l-MUT!";

	protected String spellCase3 = "Peppi ħlef quddiem l-avukat.";
	
	protected String spellSolution3 = "Peppi ħalef quddiem l-avukat.";
	
	protected String spellCase4 = "Peppi ħalef in-nagħaġ.";
	
	protected String spellSolution4 = "Peppi għalef in-nagħaġ.";
	
	public MLRSTests(String name) {
		super(name);
	}

	protected void printList(Iterable<String> strings) {
		for (String s : strings) {
			System.out.println(s);
		}
	}

	protected void printArray(String[] array) {
		for (String s : array) {
			System.out.println(s);
		}
	}

}

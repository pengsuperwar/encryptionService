package union.counter.api;

public class UnionPkForm {
	private String pkMod = null;
	private String pkEvl = null;
	
	
	UnionPkForm() {}
	/*
	���ܣ�������תΪasn1��ʽ�ĳ���
	���룺
		len:����
	
	����:
		asn1Len�������ַ������������
	*/
	byte [] UnionAsn1Len(int len)
	{
		int ret = 0;
		byte [ ] buff = new byte[10];
		byte [ ] asn1Len = null;
		if(len > 65535)
		{
			return null;
		}
		if (len > 255)
		{
			buff[0] = (byte)0x82;
			buff[1] = (byte)((len & 0xFF00) >> 8);
			buff[2] = (byte)(len & 0x00FF);
			ret  =3;
		}
		else {
			if ((len & 0x80) != 0)
			{
				buff[0] = (byte)0x81;
				buff[1] = (byte)len;
				ret = 2;
			}
			else {
				buff[0] = (byte)len;
				ret = 1;
			}
		}
		asn1Len = new byte[ret];
		System.arraycopy(buff, 0, asn1Len, 0, ret);
		return asn1Len;
	}
	
	public String byte2hex(byte[] b) { //������ת�ַ���
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
            if (n < b.length - 1) {
                hs = hs + "";
            }
        }
        return hs.toUpperCase();
    }

     public byte[] hex2byte(String str) { //�ַ���ת������
        int len = str.length();
        String stmp = null;
        byte bt[] = new byte[len / 2];
        for (int n = 0; n < len / 2; n++) {
            stmp = str.substring(n * 2, n * 2 + 2);
            bt[n] = (byte) (java.lang.Integer.parseInt(stmp, 16));
        }
        return bt;
    }
     
	/*
	����:����Կģ��ָ��ƴװΪDER�����ʽ�Ĺ�Կ
	����:
		pkModule:��Կģ�Ķ������ַ�
		exp:��Կָ���Ķ������ַ�
	
	����:
		DER�����ʽ�Ĺ�Կ�������ַ���
	*/
	byte [] UnionFormDerPK(byte [] pkModule,byte []exp)
	{
		byte []buff = new byte[4096];
		byte []tbuff = new byte[4096];
		byte [] tmp = null;
		byte [] derPK = null;
		int offset = 0;
		int len = 0;
		int 	ret=0;
		int lenOfPkModule = pkModule.length;
		int lenOfExp = exp.length;

		if (pkModule == null || exp == null)
		{
			return null;
		}
		offset = 0;
		tbuff[offset] = 0x02;
		offset += 1;
		tmp = UnionAsn1Len(lenOfPkModule+1);
		System.arraycopy(tmp, 0, tbuff, offset, tmp.length);
		offset += tmp.length;
		tbuff[offset] = 0x00;
		offset += 1;
		System.arraycopy(pkModule,0,tbuff, offset,lenOfPkModule);
		offset += lenOfPkModule;
		tbuff[offset] = 0x02;
		offset += 1;
		tmp = UnionAsn1Len(lenOfExp);
		System.arraycopy(tmp, 0, tbuff, offset, tmp.length);
		offset += tmp.length;
		System.arraycopy(exp,0,tbuff, offset,lenOfExp);
		
		offset += lenOfExp;
		len = offset;

		offset = 0;
		buff[offset] = 0x30; 
		offset += 1;
		tmp = UnionAsn1Len(len);
		System.arraycopy(tmp, 0, buff, offset, tmp.length);
		offset += tmp.length;
		System.arraycopy(tbuff, 0, buff, offset, len);
		offset += len;
		
		derPK = new byte[offset];
		
		System.arraycopy(buff, 0, derPK, 0, offset);
		
		return derPK;
	}
	
	/*
	����:����Կģ��ָ��ƴװΪDER�����ʽ�Ĺ�Կ
		�˺�����UnionFormDerPK������Ϊ,�ú��������롢����ַ���Ϊ��չ�ɼ��ַ���
	����:
		pkModule:��Կģ����չ�ɼ��ַ�
		exp:��Կָ������չ�ɼ��ַ�
	����:
		derPK:DER�����ʽ�Ĺ�Կ��չ�ɼ��ַ���
	*/
	String UnionFormDerPKWithAscHex(String pkModule,String exp)
	{
		int ret = 0;
		int len = 0;
		String derPK = null;
		byte [] pkModule_bcdhex = null;
		byte [] exp_bcdhex = null;
		byte [] derPK_bcdhex = null;
		int lenOfPkModule = 0;
		int lenOfExp = 0;
		
		if (pkModule == null || exp == null )
			return null;
		
		pkModule_bcdhex = hex2byte(pkModule);
		lenOfPkModule = pkModule_bcdhex.length;
		exp_bcdhex = hex2byte(exp);
		lenOfExp = exp_bcdhex.length;
		derPK_bcdhex = UnionFormDerPK(pkModule_bcdhex,exp_bcdhex);
		derPK = byte2hex(derPK_bcdhex);
		return derPK;
	}
	
	/*
	����:����Կģ��ָ��ƴװΪDER�����ʽ�Ĺ�Կ
		�˺�����UnionFormDerPK������Ϊ,�ú��������롢����ַ���Ϊ��չ�ɼ��ַ���
	����:
		pkModule:��Կģ����չ�ɼ��ַ�
	����:
		derPK:ָ��Ϊ65537��DER�����ʽ�Ĺ�Կ��չ�ɼ��ַ���
	
	*/
	String UnionFormDerPKWithAscHex(String pkModule)
	{
		String exp = "010001";   //ָ��Ϊ65537
		return UnionFormDerPKWithAscHex(pkModule,exp);
	}
	
//	 ��DER��ʽ��PK,ȡ�����PK����ָ��
	String UnionGetPKOutOfRacalHsmCmdReturnStr(String racalPK)
	{
		int	offset;
		int	i;
		int	lenOfNextPart;
		int	bitsOfLenFlag;
		int	lenOfStr;
		int	pkStrOffset = 0;
		byte [] racalPKStr = null;
		byte [] racalEvalStr = null;
		
		String pk;
		
		if (racalPK == null)
		{
			return null;
		}
		
		racalPKStr = hex2byte(racalPK);
		
		// ��ʼ��־λ
		offset = 0;
		if (racalPKStr[offset] != 0x30)
		{
			return null;
		}
		offset++;
		
		// �ж���һ���ֵĳ���
		if ((racalPKStr[offset]&0xFF) <= 0x80)
		{
			lenOfNextPart = (racalPKStr[offset]&0xFF);
			offset++;
		}
		else
		{
			// ����ָʾλ
			bitsOfLenFlag = (racalPKStr[offset]&0xFF) - 0x80;
			offset++;
			for (i = 0,lenOfNextPart = 0; i < bitsOfLenFlag; i++,offset++)
				lenOfNextPart += (racalPKStr[offset]&0xFF);
		}
		lenOfStr = lenOfNextPart + offset;
		
		
		// �ж��ǲ��Ƿָ�λ
		if ((racalPKStr[offset]&0xFF) != 0x02)
		{
			return null;
		}
		offset++;
		lenOfNextPart--;
		
		// �ж�PK�ĳ���	
		int lenOfPK = 0;
		if ((racalPKStr[offset]&0xFF) <= 0x80)
		{
			lenOfPK = (racalPKStr[offset]&0xFF);
			offset++;
		}
		else
		{
			// ����ָʾλ
			bitsOfLenFlag = (racalPKStr[offset]&0xFF) - 0x80;
			offset++;
			for (i = 0,lenOfPK = 0; i < bitsOfLenFlag; i++,offset++)	
				lenOfPK += (racalPKStr[offset]&0xFF);		
		}
		
		// 2007/11/15 ����
		while (lenOfPK % 8 != 0)
		{
			if ((racalPKStr[offset]&0xFF) != 0x00)
			{
				return null;
			}
			offset++;
			--(lenOfPK);
		}
		
		byte [] LPk = new byte[lenOfPK];
		System.arraycopy(racalPKStr, offset, LPk, 0, lenOfPK);
		pk = byte2hex(LPk);
		pkMod = pk;
		offset += lenOfPK;
		int lenOfEval = 0;
		if (racalPKStr[offset] != 0x02)
			return null;
		offset += 1;
		lenOfEval = racalPKStr[offset];
		offset += 1;
		byte [] LPkEval = new byte[lenOfEval];
		System.arraycopy(racalPKStr, offset, LPkEval, 0, lenOfEval);
		pkEvl = byte2hex(LPkEval);
		return pk;
	}
	
	public String getPkEvl() {
		return pkEvl;
	}
	public void setPkEvl(String pkEvl) {
		this.pkEvl = pkEvl;
	}
	public String getPkMod() {
		return pkMod;
	}
	public void setPkMod(String pkMod) {
		this.pkMod = pkMod;
	}
	
	public static void main(String[] args) throws Exception {
		// TODO code application logic here
		UnionPkForm pkForm = new UnionPkForm();
		String pkDer = pkForm.UnionFormDerPKWithAscHex("A2FCF9F222B1DDA7E7FD378A5E945FF83A37AAD449F09FAFB858020E84908765B121456E19CBDDDC18980557A3EAE25052A4DCCFCE114ABAA7F4CDB3F02E8932426BF83F5B6B2CFA91058A6EE5613A7B226926E82F096BDAEF9659CA49D4C7CFE28DF332BD8C53990500B33C3554A126F819C256901BA62ACDCB6D8DEE186443","010001");
		System.out.println("pkDer=["+ pkDer + "]");  
		String pk = pkForm.UnionGetPKOutOfRacalHsmCmdReturnStr(pkDer);
		System.out.println("pk=["+ pk + "]");  
		System.out.println("pk=["+ pkForm.pkMod + "]");  
		System.out.println("eval=["+ pkForm.getPkEvl() + "]"); 
	}
	

}

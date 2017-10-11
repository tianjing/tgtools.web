package tgtools.web.entity;

/**
 * bsgrid实体类
 * @author marjuce
 *
 */
public class BSGridDataEntity extends GridDataEntity{
	public BSGridDataEntity() {
		setSussecc(true);
		setCurPage(1);
		setTotalRows(-1);
	}

/**
 * String转换成json对象
 * @return
 */
	public String toJson() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"success\" : {res},\"totalRows\" :{count},\"curPage\" : {curPage},\"data\" :");
		if (null != getData()) {
			sb.append(getData().toJson());
		} else if (null != getError()) {
			sb.append("\""+getError()+"\"");
		} else {
			sb.append("[]");
		}
		sb.append("}");
		String result = sb.toString();
		result = result.replace("{res}", Boolean.toString(this.getSussecc()));
		result = result.replace("{count}", getCount());
		result = result.replace("{curPage}",
				Integer.toString(this.getCurPage()));

		return result;
	}
/**
 * 获取总行数
 * @return
 */
	private String getCount() {
		if (getTotalRows() > -1) {
			return Integer.toString(getTotalRows());
		} else {
			return null == this.getData() ? "0" : Integer.toString(this.getData()
					.getRows().size());
		}

	}
	
}

package Game;

public class Report 
{
	public int whitePoints;
	public int blackPoints;
	public Report(int wp, int bp)
	{
		setWhitePoints(wp);
		setBlackPoints(bp);
	}
	public int getWhitePoints() {
		return whitePoints;
	}
	public void setWhitePoints(int whitePoints) {
		this.whitePoints = whitePoints;
	}
	public int getBlackPoints() {
		return blackPoints;
	}
	public void setBlackPoints(int blackPoints) {
		this.blackPoints = blackPoints;
	}
}

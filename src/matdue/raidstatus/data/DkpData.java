package matdue.raidstatus.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class DkpData {
	
	private static final Pattern OPENING_PATTERN = Pattern.compile("\\s*\\{.*");
	private static final Pattern CLOSING_PATTERN = Pattern.compile("\\s*\\}.*");
	
	public Map<String, Player> players;
	public Collection<Raid> raids;
	
	public DkpData() {
		players = new HashMap<String, Player>();
		raids = new ArrayList<Raid>();
	}
	
	public void parse(BufferedReader reader) throws IOException {
		String line;
		
		// Skip HTML code to start of data
		while ((line = reader.readLine()) != null) {
			if ("--[START]".equals(line)) {
				break;
			}
		}
		if (line == null) {
			return;
		}
		
		// Parse blocks
		while ((line = reader.readLine()) != null) {
			if ("gdkp = {".equals(line)) {
				parsePlayers(reader);
			} else if ("GetDKPRaidPlaner = {".equals(line)) {
				parsePlaner(reader);
			}
		}
	}

	void parsePlayers(BufferedReader reader) throws IOException {
		players = new HashMap<String, Player>();
		reader.readLine();  // Skip ["players"] = {
		
		// Player
		String line;
		while ((line = reader.readLine()) != null) {
			if (CLOSING_PATTERN.matcher(line).matches()) {
				break;
			}
			
			Player player = new Player();
			
			int posEqualsSign = line.indexOf('=');
			if (posEqualsSign == -1) {
				return;
			}
			player.name = parseString(line.substring(0, posEqualsSign));
			
			while ((line = reader.readLine()) != null) {
				if (CLOSING_PATTERN.matcher(line).matches()) {
					break;
				}
				
				posEqualsSign = line.indexOf('=');
				if (posEqualsSign == -1) {
					break;
				}
				
				String name = parseString(line.substring(0, posEqualsSign));
				if ("class".equals(name)) {
					player.className = parseString(line.substring(posEqualsSign + 1));
				} else if ("dkp_current".equals(name)) {
					player.currentDkp = parseDecimal(line.substring(posEqualsSign + 1));
				}
			}
			
			players.put(player.name, player);
		}
		
		reader.readLine();  // Skip closing }
	}

	void parsePlaner(BufferedReader reader) throws IOException {
		raids = new ArrayList<Raid>();
		reader.readLine();  // Skip ["raid"] = {
		
		// Raid
		String line;
		while ((line = reader.readLine()) != null) {
			if (CLOSING_PATTERN.matcher(line).matches()) {
				break;
			}
			
			Raid raid = new Raid();
			while ((line = reader.readLine()) != null) {
				if (CLOSING_PATTERN.matcher(line).matches()) {
					break;
				}
				
				int posEqualsSign = line.indexOf('=');
				if (posEqualsSign == -1) {
					break;
				}
				
				String name = parseString(line.substring(0, posEqualsSign));
				if ("raid_name".equals(name)) {
					raid.name = parseString(line.substring(posEqualsSign + 1));
				} else if ("raid_icon".equals(name)) {
					raid.icon = parseString(line.substring(posEqualsSign + 1));
				} else if ("raid_note".equals(name)) {
					raid.note = parseString(line.substring(posEqualsSign + 1));
				} else if ("raid_date".equals(name)) {
					raid.start = parseDate(line.substring(posEqualsSign + 1));
				} else if ("raid_date_finish".equals(name)) {
					raid.finish = parseDate(line.substring(posEqualsSign + 1));
				} else if ("raid_date_invite".equals(name)) {
					raid.invite = parseDate(line.substring(posEqualsSign + 1));
				} else if ("raid_date_subscription".equals(name)) {
					raid.subscription = parseDate(line.substring(posEqualsSign + 1));
				} else if ("raid_attendees".equals(name)) {
					String attendees = parseString(line.substring(posEqualsSign + 1));
					raid.attendees = parseInt(attendees);
				} else if ("raid_members".equals(name)) {
					raid.raidMembers = parseMembers(reader);
				} else if ("raid_classes".equals(name)) {
					raid.raidClasses = parseRaidClasses(reader);
				} else if ("raid_classes_role".equals(name)) {
					skipBlock(reader);
				}
			}
			
			raids.add(raid);
		}
	}
	
	void skipBlock(BufferedReader reader) throws IOException {
		int indention = 0;
		
		String line;
		while ((line = reader.readLine()) != null) {
			if (OPENING_PATTERN.matcher(line).matches()) {
				++indention;
			} else if (CLOSING_PATTERN.matcher(line).matches()) {
				--indention;
				if (indention < 0) {
					break;
				}
			}
		}
	}
	
	Collection<RaidMember> parseMembers(BufferedReader reader) throws IOException {
		Collection<RaidMember> members = new ArrayList<RaidMember>();
		
		String line;
		while ((line = reader.readLine()) != null) {
			if (OPENING_PATTERN.matcher(line).matches()) {
				members.add(parseMember(reader));
			} else if (CLOSING_PATTERN.matcher(line).matches()) {
				break;
			}
		}
		
		return members;
	}
	
	RaidMember parseMember(BufferedReader reader) throws IOException {
		RaidMember member = new RaidMember();
		
		String line;
		while ((line = reader.readLine()) != null) {
			if (CLOSING_PATTERN.matcher(line).matches()) {
				break;
			}
			
			int posEqualsSign = line.indexOf('=');
			if (posEqualsSign == -1) {
				break;
			}
			
			String name = parseString(line.substring(0, posEqualsSign));
			if ("player".equals(name)) {
				member.player = players.get(parseString(line.substring(posEqualsSign + 1)));
			} else if ("role".equals(name)) {
				member.role = parseString(line.substring(posEqualsSign + 1));
			} else if ("note".equals(name)) {
				member.note = parseString(line.substring(posEqualsSign + 1));
			} else if ("subscribed".equals(name)) {
				member.subscribed = parseInt(line.substring(posEqualsSign + 1));
			}
		}
		
		return member;
	}
	
	Collection<RaidClass> parseRaidClasses(BufferedReader reader) throws IOException {
		Collection<RaidClass> raidClasses = new ArrayList<RaidClass>();
		
		String line;
		while ((line = reader.readLine()) != null) {
			if (OPENING_PATTERN.matcher(line).matches()) {
				raidClasses.add(parseRaidClass(reader));
			} else if (CLOSING_PATTERN.matcher(line).matches()) {
				break;
			}
		}
		
		return raidClasses;
	}
	
	RaidClass parseRaidClass(BufferedReader reader) throws IOException {
		RaidClass raidClass = new RaidClass();
		
		String line;
		while ((line = reader.readLine()) != null) {
			if (CLOSING_PATTERN.matcher(line).matches()) {
				break;
			}
			
			int posEqualsSign = line.indexOf('=');
			if (posEqualsSign == -1) {
				break;
			}
			
			String name = parseString(line.substring(0, posEqualsSign));
			if ("class_name".equals(name)) {
				raidClass.className = parseString(line.substring(posEqualsSign + 1));
			} else if ("class_count".equals(name)) {
				raidClass.count = parseInt(line.substring(posEqualsSign + 1));
			} 
		}
		
		return raidClass;
	}

	String parseString(String luaCode) throws UnsupportedEncodingException {
		int luaCodeLength = luaCode.length();
		StringBuilder result = new StringBuilder(luaCodeLength);
		
		boolean insideQuotation = false;
		int idx = 0;
		parserLoop:
		while (idx < luaCodeLength) {
			char c = luaCode.charAt(idx++);
			
			if (insideQuotation) {
				switch (c) {
				case '"':
					insideQuotation = false;
					break parserLoop;
					
				case '\\':
					c = luaCode.charAt(idx++);
					switch (c) {
					case '\\':
					case '"':
					case '\'':
					case '[':
					case ']':
						result.append(c);
						break;
					
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						// Collect numbers to form one or more unicode chars
						ArrayList<Integer> bytes = new ArrayList<Integer>();
						bytes.add(Integer.valueOf(luaCode.substring(idx - 1, idx + 2)));
						idx += 2;
						while (luaCode.charAt(idx) == '\\' && luaCode.charAt(idx + 1) >= '0' && luaCode.charAt(idx + 1) <= '9') {
							bytes.add(Integer.valueOf(luaCode.substring(idx + 1, idx + 4)));
							idx += 4;
						}
						byte[] byteArray = new byte[bytes.size()];
						for (int i = 0; i < bytes.size(); ++i) {
							byteArray[i] = (byte)(bytes.get(i) & 0xFF);
						}
						String unicodes = new String(byteArray, "UTF-8");
						result.append(unicodes);
						break;
					}
					break;
					
				default:
					result.append(c);
				}
			} else {
				switch (c) {
				case '"':
					insideQuotation = true;
					break;
				}
			}
		}
		
		return result.toString();
	}
	
	BigDecimal parseDecimal(String luaCode) {
		BigDecimal result = BigDecimal.ZERO;
		
		int luaCodeLength = luaCode.length();
		StringBuilder number = new StringBuilder(luaCodeLength);
		int idx = 0;
		parserLoop:
		while (idx < luaCodeLength) {
			char c = luaCode.charAt(idx++);
			switch (c) {
			case ' ':
				break;
				
			case '-':
				if (number.length() == 0) {
					number.append(c);
					break;
				} else {
					break parserLoop;
				}
				
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case '.':
				number.append(c);
				break;
				
			default:
				break parserLoop;
			}
		}
		
		if (number.length() != 0) {
			result = new BigDecimal(number.toString());
		}
		
		return result;
	}
	
	int parseInt(String luaCode) {
		int result = 0;
		
		int luaCodeLength = luaCode.length();
		StringBuilder number = new StringBuilder(luaCodeLength);
		int idx = 0;
		parserLoop:
		while (idx < luaCodeLength) {
			char c = luaCode.charAt(idx++);
			switch (c) {
			case ' ':
				break;
				
			case '-':
				if (number.length() == 0) {
					number.append(c);
					break;
				} else {
					break parserLoop;
				}
				
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				number.append(c);
				break;
				
			default:
				break parserLoop;
			}
		}
		
		if (number.length() != 0) {
			result = Integer.parseInt(number.toString());
		}
		
		return result;
	}
	
	Date parseDate(String luaCode) {
		Date result = null;
		
		long dateInSeconds = parseInt(luaCode);
		result = new Date(dateInSeconds * 1000);
		
		return result;
	}
	
}

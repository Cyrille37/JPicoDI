package picoDI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Usage:
 * 
 * PicoDI pdi = new PicoDI(); pdi.load("somepath/somefile.json"); AClass
 * anObject = (AClass) pdi.getObject("ObjectName");
 * 
 * Json File format:
 * 
 * For json file format, have a look in picoDI.tests.fixtures
 * 
 * @author cyrille
 */
public class PicoDI {

	public static void main(String[] args) {

		PicoDI pdi = new PicoDI();
		pdi.load("tests/fixtures/config02.json");
	}

	/**
	 * Contains all loaded objects
	 */
	HashMap<String, Object> availablesObjects;

	public PicoDI() {
		availablesObjects = new HashMap<String, Object>();
	}

	/**
	 * Retrieve an Object by it's name. You need to cast it has the attended
	 * type.
	 * 
	 * @param name
	 *            The Object name, in the json configuration
	 * @return An Object that you need to well cast
	 */
	public Object getObject(String name) {
		return this.availablesObjects.get(name);
	}

	/**
	 * To load Objects from the json configuration file.
	 * 
	 * @param filename
	 *            The json configuration file name
	 */
	public void load(String filename) {
		this.load(filename, true);
	}

	public void load(String filename, boolean append) {

		if (!append) {
			availablesObjects.clear();
		}
		loadClasses(loadJson(filename));
	}

	@SuppressWarnings("rawtypes")
	protected void loadClasses(ArrayList<ContainerItem> items) {
		ClassLoader classLoader = PicoDI.class.getClassLoader();
		try {
			for (ContainerItem ci : items) {
				Class aClass = classLoader.loadClass(ci.classname);
				boolean objectCreated = false;
				for (Constructor constructor : aClass.getConstructors()) {
					if (ci.params == null
							&& constructor.getParameterTypes().length == 0) {
						// Ok, default constructor
						objectCreated = true;
						availablesObjects.put(ci.name, aClass.newInstance());
						break;
					} else if (ci.params != null
							&& (ci.params.size() == constructor
									.getParameterTypes().length)) {
						// constructor with parameters
						ArrayList<Object> wellTypedValues = wellTypeValues(
								ci.params, constructor.getParameterTypes());
						if (wellTypedValues != null) {
							objectCreated = true;
							availablesObjects.put(ci.name, constructor
									.newInstance(wellTypedValues.toArray()));
							break;
						}
					}
				}
				if (!objectCreated)
					throw new RuntimeException(
							"No matching constructor found for '" + ci.name
									+ "'");
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SuppressWarnings("rawtypes")
	protected ArrayList<Object> wellTypeValues(ArrayList<Object> params,
			Class[] parameterTypes) {

		ArrayList<Object> wellTypedValues = new ArrayList<Object>();
		for (int i = 0; i < params.size(); i++) {
			Class pc = params.get(i).getClass();
			Object pv = params.get(i);
			Class cc = parameterTypes[i];
			if (pc == Long.class) {
				if (cc == int.class) {
					wellTypedValues.add(Integer.valueOf(pv.toString())
							.intValue());
				} else if (cc == Integer.class) {
					wellTypedValues.add(Integer.valueOf(pv.toString()));
				} else if (cc == long.class) {
					wellTypedValues.add(((Long) pv).longValue());
				} else if (cc == Long.class) {
					wellTypedValues.add(pv);
				}
			} else if (pc == Boolean.class) {
				if (cc == Boolean.class) {
					wellTypedValues.add(pv);
				}
			} else if (pc == String.class) {
				if (cc == String.class) {
					wellTypedValues.add(pv);
				}
			} else if (pc == null) {
				wellTypedValues.add(null);
			}
		}
		if (wellTypedValues.size() == parameterTypes.length)
			return wellTypedValues;
		return null;
	}

	protected ArrayList<ContainerItem> loadJson(String filename) {
		ArrayList<ContainerItem> items = null;
		String json = fileToString(filename);
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(json);
			JSONArray array = (JSONArray) obj;
			items = new ArrayList<PicoDI.ContainerItem>(array.size());
			for (Object o : array) {

				ContainerItem ci = new ContainerItem();
				JSONObject o2 = (JSONObject) o;
				ci.name = o2.get("name").toString();
				ci.classname = o2.get("class").toString();
				if (o2.get("params") != null) {
					JSONArray array2 = (JSONArray) o2.get("params");
					ci.params = new ArrayList<Object>(array2.size());
					for (Object o3 : array2) {
						ci.params.add(o3);
					}
				}
				items.add(ci);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return items;
	}

	protected class ContainerItem {
		String name;
		String classname;
		ArrayList<Object> params;
	}

	protected static String fileToString(String filename) {

		try {
			File file = new File(filename);
			if (file.length() > Integer.MAX_VALUE)
				throw new RuntimeException("File is to large : "
						+ file.length() + " bytes");
			StringBuffer sb = new StringBuffer((int) file.length());
			BufferedReader br = new BufferedReader(new FileReader(file));
			String s;
			while ((s = br.readLine()) != null) {
				sb.append(s);
			}
			br.close();
			return sb.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new RuntimeException("Failed to read file : " + filename);
	}
}

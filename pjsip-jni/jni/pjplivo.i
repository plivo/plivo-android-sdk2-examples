%module (directors="1") plivo
%{
#include <cassert>
#include <iostream>
%}

%include "std_string.i"
%include "std_map.i"

%{
#include "plivo_app_callback.h"


#ifdef __cplusplus
extern "C" {
#endif
	int plivoStart();
	void plivoDestroy();
	int plivoRestart();
	void setCallbackObject(PlivoAppCallback* callback);	
#ifdef __cplusplus
}
#endif
%}



// 1.
%rename (size_impl) std::map<std::string,std::string>::size;
%rename (isEmpty) std::map<std::string,std::string>::empty;

%typemap(jstype) std::map<std::string, std::string> "java.util.Map<String,String>"
%typemap(javain,pre="    MapType temp$javainput = $javaclassname.convertMap($javainput);",pgcppname="temp$javainput") std::map<std::string, std::string> "$javaclassname.getCPtr(temp$javainput)"
%typemap(javacode) std::map<std::string, std::string> %{
  static $javaclassname convertMap(Map<String,String> in) {
    // 2.
    if (in instanceof $javaclassname) {
      return ($javaclassname)in;
    }

    $javaclassname out = new $javaclassname();
    for (Map.Entry<String, String> entry : in.entrySet()) {
      out.set(entry.getKey(), entry.getValue());
    }
    return out;
  }

  // 3.
  public Set<Map.Entry<String,String>> entrySet() {
    HashSet<Map.Entry<String,String>> ret = new HashSet<Map.Entry<String,String>>(size());
    String array[] = new String[size()];
    all_keys(array);
    for (String key: array) {
      ret.add(new MapTypeEntry(key,this));
    }
    return ret;
  }

  public Collection<String> values() {
    String array[] = new String[size()];
    all_values(array);
    return new ArrayList<String>(Arrays.asList(array));
  }

  public Set<String> keySet() {
    String array[] = new String[size()];
    all_keys(array);
    return new HashSet<String>(Arrays.asList(array));
  }

  // 4.
  public String remove(Object key) {
    final String ret = get(key);
    remove((String)key);
    return ret;
  }

  public String put(String key, String value) {
    final String ret = has_key(key) ? get(key) : null;
    set(key, value);
    return ret;
  }

  // 5.
  public int size() {
    return (int)size_impl();
  }
%}

// 6.
%typemap(javaimports) std::map<std::string, std::string> "import java.util.*;";
// 7.
%typemap(javabase) std::map<std::string, std::string> "AbstractMap<String, String>";

// 8.
%{
template <typename K, typename V>
struct map_entry {
  const K key;
  map_entry(const K& key, std::map<K,V> *owner) : key(key), m(owner) {
  }
  std::map<K,V> * const m;
};
%}

// 9.
template <typename K, typename V>
struct map_entry {
  const K key;
  %extend {
    V getValue() const {
      return (*$self->m)[$self->key];
    }

    V setValue(const V& n) const {
      const V old = (*$self->m)[$self->key];
      (*$self->m)[$self->key] = n;
      return old;
    }
  }
  map_entry(const K& key, std::map<K,V> *owner);
};

// 10.
%typemap(javainterfaces) map_entry<std::string, std::string> "java.util.Map.Entry<String,String>";
// 11.
%typemap(in,numinputs=0) JNIEnv * %{
  $1 = jenv;
%}

// 12.
%extend std::map<std::string, std::string> {
  void all_values(jobjectArray values, JNIEnv *jenv) const {
    assert((jsize)$self->size() == jenv->GetArrayLength(values));
    jsize pos = 0;
    for (std::map<std::string, std::string>::const_iterator it = $self->begin();
         it != $self->end();
         ++it) {
       jenv->SetObjectArrayElement(values, pos++, jenv->NewStringUTF(it->second.c_str()));
    }
  }

  void all_keys(jobjectArray keys, JNIEnv *jenv) const {
    assert((jsize)$self->size() == jenv->GetArrayLength(keys));
    jsize pos = 0;
    for (std::map<std::string, std::string>::const_iterator it = $self->begin();
         it != $self->end();
         ++it) {
       jenv->SetObjectArrayElement(keys, pos++, jenv->NewStringUTF(it->first.c_str()));
    }
  }
}
%template(MapType) std::map<std::string, std::string>;
%template(MapTypeEntry) map_entry<std::string, std::string>;


int plivoStart();
void plivoDestroy();
int plivoRestart();

/* turn on director wrapping PlivoAppCallback */
%feature("director") PlivoAppCallback;

%include "plivo_app_callback.h"

void setCallbackObject(PlivoAppCallback* callback);


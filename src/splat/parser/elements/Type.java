package splat.parser.elements;

public class Type {
  private String name;

  public Type(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
  
  public String toString() {
    return name;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Type type = (Type) obj;
    return name != null ? name.equals(type.name) : type.name == null;
  }
  
  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }
}

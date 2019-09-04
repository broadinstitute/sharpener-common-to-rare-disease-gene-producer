package apimodels;

import apimodels.GeneInfo;
import apimodels.Property;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.*;
import java.util.Set;
import javax.validation.*;
import java.util.Objects;
import javax.validation.constraints.*;
/**
 * TransformerQuery
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaPlayFrameworkCodegen", date = "2019-09-04T17:57:08.431Z")

@SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
public class TransformerQuery   {
  @JsonProperty("genes")
  private List<GeneInfo> genes = null;

  @JsonProperty("controls")
  private List<Property> controls = new ArrayList<>();

  public TransformerQuery genes(List<GeneInfo> genes) {
    this.genes = genes;
    return this;
  }

  public TransformerQuery addGenesItem(GeneInfo genesItem) {
    if (genes == null) {
      genes = new ArrayList<>();
    }
    genes.add(genesItem);
    return this;
  }

   /**
   * List of genes that will be transformed. Required for expanders and filters; should be omitted for producers.
   * @return genes
  **/
  @Valid
  public List<GeneInfo> getGenes() {
    return genes;
  }

  public void setGenes(List<GeneInfo> genes) {
    this.genes = genes;
  }

  public TransformerQuery controls(List<Property> controls) {
    this.controls = controls;
    return this;
  }

  public TransformerQuery addControlsItem(Property controlsItem) {
    controls.add(controlsItem);
    return this;
  }

   /**
   * Values that control the behavior of the transformer. Names of the controls must match the names specified in the transformer's definition and values must match types (and possibly  allowed_values) specified in the transformer's definition.
   * @return controls
  **/
  @NotNull
@Valid
  public List<Property> getControls() {
    return controls;
  }

  public void setControls(List<Property> controls) {
    this.controls = controls;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TransformerQuery transformerQuery = (TransformerQuery) o;
    return Objects.equals(genes, transformerQuery.genes) &&
        Objects.equals(controls, transformerQuery.controls);
  }

  @Override
  public int hashCode() {
    return Objects.hash(genes, controls);
  }

  @SuppressWarnings("StringBufferReplaceableByString")
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransformerQuery {\n");
    
    sb.append("    genes: ").append(toIndentedString(genes)).append("\n");
    sb.append("    controls: ").append(toIndentedString(controls)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}


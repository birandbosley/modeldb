// THIS FILE IS AUTO-GENERATED. DO NOT EDIT
package ai.verta.modeldb.versioning.autogenerated._public.modeldb.versioning.model;

import ai.verta.modeldb.ModelDBException;
import ai.verta.modeldb.versioning.*;
import ai.verta.modeldb.versioning.blob.visitors.Visitor;
import java.util.*;
import java.util.function.Function;

public class HyperparameterSetConfigDiff {
  public DiffStatusEnumDiffStatus Status;
  public String Name;
  public ContinuousHyperparameterSetConfigBlob ContinuousA;
  public DiscreteHyperparameterSetConfigBlob DiscreteA;
  public ContinuousHyperparameterSetConfigBlob ContinuousB;
  public DiscreteHyperparameterSetConfigBlob DiscreteB;

  public HyperparameterSetConfigDiff() {
    this.Status = null;
    this.Name = null;
    this.ContinuousA = null;
    this.DiscreteA = null;
    this.ContinuousB = null;
    this.DiscreteB = null;
  }

  public HyperparameterSetConfigDiff setStatus(DiffStatusEnumDiffStatus value) {
    this.Status = value;
    return this;
  }

  public HyperparameterSetConfigDiff setName(String value) {
    this.Name = value;
    return this;
  }

  public HyperparameterSetConfigDiff setContinuousA(ContinuousHyperparameterSetConfigBlob value) {
    this.ContinuousA = value;
    return this;
  }

  public HyperparameterSetConfigDiff setDiscreteA(DiscreteHyperparameterSetConfigBlob value) {
    this.DiscreteA = value;
    return this;
  }

  public HyperparameterSetConfigDiff setContinuousB(ContinuousHyperparameterSetConfigBlob value) {
    this.ContinuousB = value;
    return this;
  }

  public HyperparameterSetConfigDiff setDiscreteB(DiscreteHyperparameterSetConfigBlob value) {
    this.DiscreteB = value;
    return this;
  }

  public static HyperparameterSetConfigDiff fromProto(
      ai.verta.modeldb.versioning.HyperparameterSetConfigDiff blob) {
    if (blob == null) {
      return null;
    }

    HyperparameterSetConfigDiff obj = new HyperparameterSetConfigDiff();
    {
      Function<ai.verta.modeldb.versioning.HyperparameterSetConfigDiff, DiffStatusEnumDiffStatus>
          f = x -> DiffStatusEnumDiffStatus.fromProto(blob.getStatus());
      obj.Status = f.apply(blob);
    }
    {
      Function<ai.verta.modeldb.versioning.HyperparameterSetConfigDiff, String> f =
          x -> (blob.getName());
      obj.Name = f.apply(blob);
    }
    {
      Function<
              ai.verta.modeldb.versioning.HyperparameterSetConfigDiff,
              ContinuousHyperparameterSetConfigBlob>
          f = x -> ContinuousHyperparameterSetConfigBlob.fromProto(blob.getContinuousA());
      obj.ContinuousA = f.apply(blob);
    }
    {
      Function<
              ai.verta.modeldb.versioning.HyperparameterSetConfigDiff,
              DiscreteHyperparameterSetConfigBlob>
          f = x -> DiscreteHyperparameterSetConfigBlob.fromProto(blob.getDiscreteA());
      obj.DiscreteA = f.apply(blob);
    }
    {
      Function<
              ai.verta.modeldb.versioning.HyperparameterSetConfigDiff,
              ContinuousHyperparameterSetConfigBlob>
          f = x -> ContinuousHyperparameterSetConfigBlob.fromProto(blob.getContinuousB());
      obj.ContinuousB = f.apply(blob);
    }
    {
      Function<
              ai.verta.modeldb.versioning.HyperparameterSetConfigDiff,
              DiscreteHyperparameterSetConfigBlob>
          f = x -> DiscreteHyperparameterSetConfigBlob.fromProto(blob.getDiscreteB());
      obj.DiscreteB = f.apply(blob);
    }
    return obj;
  }

  public ai.verta.modeldb.versioning.HyperparameterSetConfigDiff.Builder toProto() {
    ai.verta.modeldb.versioning.HyperparameterSetConfigDiff.Builder builder =
        ai.verta.modeldb.versioning.HyperparameterSetConfigDiff.newBuilder();
    {
      if (this.Status != null) {
        Function<ai.verta.modeldb.versioning.HyperparameterSetConfigDiff.Builder, Void> f =
            x -> {
              builder.setStatus(this.Status.toProto());
              return null;
            };
        f.apply(builder);
      }
    }
    {
      if (this.Name != null) {
        Function<ai.verta.modeldb.versioning.HyperparameterSetConfigDiff.Builder, Void> f =
            x -> {
              builder.setName(this.Name);
              return null;
            };
        f.apply(builder);
      }
    }
    {
      if (this.ContinuousA != null) {
        Function<ai.verta.modeldb.versioning.HyperparameterSetConfigDiff.Builder, Void> f =
            x -> {
              builder.setContinuousA(this.ContinuousA.toProto());
              return null;
            };
        f.apply(builder);
      }
    }
    {
      if (this.DiscreteA != null) {
        Function<ai.verta.modeldb.versioning.HyperparameterSetConfigDiff.Builder, Void> f =
            x -> {
              builder.setDiscreteA(this.DiscreteA.toProto());
              return null;
            };
        f.apply(builder);
      }
    }
    {
      if (this.ContinuousB != null) {
        Function<ai.verta.modeldb.versioning.HyperparameterSetConfigDiff.Builder, Void> f =
            x -> {
              builder.setContinuousB(this.ContinuousB.toProto());
              return null;
            };
        f.apply(builder);
      }
    }
    {
      if (this.DiscreteB != null) {
        Function<ai.verta.modeldb.versioning.HyperparameterSetConfigDiff.Builder, Void> f =
            x -> {
              builder.setDiscreteB(this.DiscreteB.toProto());
              return null;
            };
        f.apply(builder);
      }
    }
    return builder;
  }

  public void preVisitShallow(Visitor visitor) throws ModelDBException {
    visitor.preVisitHyperparameterSetConfigDiff(this);
  }

  public void preVisitDeep(Visitor visitor) throws ModelDBException {
    this.preVisitShallow(visitor);
    visitor.preVisitDeepDiffStatusEnumDiffStatus(this.Status);
    visitor.preVisitDeepString(this.Name);
    visitor.preVisitDeepContinuousHyperparameterSetConfigBlob(this.ContinuousA);
    visitor.preVisitDeepDiscreteHyperparameterSetConfigBlob(this.DiscreteA);
    visitor.preVisitDeepContinuousHyperparameterSetConfigBlob(this.ContinuousB);
    visitor.preVisitDeepDiscreteHyperparameterSetConfigBlob(this.DiscreteB);
  }

  public HyperparameterSetConfigDiff postVisitShallow(Visitor visitor) throws ModelDBException {
    return visitor.postVisitHyperparameterSetConfigDiff(this);
  }

  public HyperparameterSetConfigDiff postVisitDeep(Visitor visitor) throws ModelDBException {
    this.Status = visitor.postVisitDeepDiffStatusEnumDiffStatus(this.Status);
    this.Name = visitor.postVisitDeepString(this.Name);
    this.ContinuousA = visitor.postVisitDeepContinuousHyperparameterSetConfigBlob(this.ContinuousA);
    this.DiscreteA = visitor.postVisitDeepDiscreteHyperparameterSetConfigBlob(this.DiscreteA);
    this.ContinuousB = visitor.postVisitDeepContinuousHyperparameterSetConfigBlob(this.ContinuousB);
    this.DiscreteB = visitor.postVisitDeepDiscreteHyperparameterSetConfigBlob(this.DiscreteB);
    return this.postVisitShallow(visitor);
  }
}

/*
 *     Dr.Rename - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2022
 *
 *     This file is part of Dr.Rename.
 *
 *     You can redistribute it and/or modify it under the terms of the GNU Affero
 *     General Public License as published by the Free Software Foundation, either
 *     version 3 of the License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but WITHOUT
 *     ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *     FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License
 *     for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package drrename.commons;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Objects;

@Slf4j
public class RenamingPath {
    protected final ObjectProperty<Path> oldPath;

    protected final StringProperty newPath;

    protected final ObjectProperty<Throwable> exception;

    protected final BooleanProperty willChange;

    protected final BooleanProperty filtered;

    protected final StringProperty fileName;

    private final ChangeListener<? super Path> listener;

    public RenamingPath(final Path path) {
        listener = new ChangeListener<Path>() {
            @Override
            public void changed(ObservableValue<? extends Path> observableValue, Path path, Path t1) {
                fileName.set(t1.getFileName().toString());
            }
        };
        this.oldPath = new SimpleObjectProperty<>();
        this.newPath = new SimpleStringProperty();
        this.fileName = new SimpleStringProperty();
        this.exception = new SimpleObjectProperty<>();
        this.willChange = new SimpleBooleanProperty();
        this.filtered = new SimpleBooleanProperty();
        oldPath.addListener(listener);
        oldPath.set(Objects.requireNonNull(path));
    }

    // FX Getter / Setter //


    public Path getOldPath() {
        return oldPath.get();
    }

    public ObjectProperty<Path> oldPathProperty() {
        return oldPath;
    }

    public void setOldPath(Path oldPath) {
        this.oldPath.set(oldPath);
    }

    public String getNewPath() {
        return newPath.get();
    }

    public StringProperty newPathProperty() {
        return newPath;
    }

    public void setNewPath(String newPath) {
        this.newPath.set(newPath);
    }

    public Throwable getException() {
        return exception.get();
    }

    public ObjectProperty<Throwable> exceptionProperty() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception.set(exception);
    }

    public boolean isWillChange() {
        return willChange.get();
    }

    public BooleanProperty willChangeProperty() {
        return willChange;
    }

    public void setWillChange(boolean willChange) {
        this.willChange.set(willChange);
    }

    public boolean isFiltered() {
        return filtered.get();
    }

    public BooleanProperty filteredProperty() {
        return filtered;
    }

    public void setFiltered(boolean filtered) {
        this.filtered.set(filtered);
    }

    public String getFileName() {
        return fileName.get();
    }

    public StringProperty fileNameProperty() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }
}

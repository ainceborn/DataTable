## 🎨 DataTable [Compose] [![](https://jitpack.io/v/ainceborn/DataTable.svg)](https://jitpack.io/#ainceborn/DataTable)

```
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url = uri("https://jitpack.io") }
		}
	}

```

```
  dependencies {
	   implementation("com.github.ainceborn:DataTable:1.0.3")
	}
```

## 🎨 Customization Options

The `StickyMatrix` /  `DataTable` composable provides several parameters to customize its appearance and behavior:

| Parameter                    | Description                                                                                  |
|------------------------------|----------------------------------------------------------------------------------------------|
| `columnHeaders`              | List of strings representing the top column headers (sticky).                               |
| `rowHeaders`                 | List of strings representing the first row headers (sticky).                                |
| `data`                       | A 2D list of strings representing the grid content (data cells).                            |
| `modifier`                   | Modifier for styling and positioning the matrix layout in your UI hierarchy.                |
| `defaultCellWidth`           | Width of each cell in the grid (default size).                                              |
| `defaultCellHeight`          | Height of each cell in the grid (default size).                                             |
| `defaultCellBackground`      | Background color of each cell in the grid layout (default color).                           |
| `staticCellContentAlignment` | Alignment of content inside the static cell (top-left corner).                              |
| `staticCellTextStyle`        | Text style for the static cell (e.g., font size, weight, color).                            |
| `staticCellText`             | Text content displayed in the static cell (e.g., a label or icon).                          |
| `columnHeaderHeight`         | Height of the column header row (sticky).                                                   |
| `columnHeaderBackground`     | Background color of the column header row (sticky).                                         |
| `columnHeaderContentAlignment` | Alignment of text inside each column header cell (e.g., center-aligned).                  |
| `rowHeaderBackground`        | Background color of the row header column (sticky).                                         |
| `rowHeaderContentAlignment`  | Alignment of text inside each row header cell (e.g., center-aligned).                       |
| `dataBoxColor`               | Background color of data cells in the grid layout (non-sticky).                             |
| `dataBoxContentAlignment`    | Alignment of text inside each data cell (e.g., left-aligned or right-aligned).              |
| `horizontalCellDividerColor` | Color of horizontal dividers between rows in the grid layout (optional styling option).     |
| `verticalCellDividerColor`   | Color of vertical dividers between columns in the grid layout (optional styling option).     |

---


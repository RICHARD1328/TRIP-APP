import os
import sys
import docx
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import qn, nsdecls
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.enum.section import WD_SECTION

# Import report contents
from report_content_front import FRONT_MATTER, PBL_TABLE_0, PBL_TABLE_1, ABBREVIATIONS
from report_chapters_1_4 import CHAPTER_1, CHAPTER_2, TABLE_2_3, CHAPTER_3, TABLE_3_1, CHAPTER_4, TABLE_4_1, TABLE_4_2
from report_chapters_5_8 import CHAPTER_5, CHAPTER_6, TABLE_6_1, TABLE_6_2, CHAPTER_7, TABLE_7_1, TABLE_7_2, TABLE_7_3, CHAPTER_8, TABLE_8_1
from report_chapters_9_14 import CHAPTER_9, TABLE_9_1, TABLE_9_2, TABLE_9_3, TABLE_9_5, CHAPTER_10, CHAPTER_11, CHAPTER_12, CHAPTER_13, CHAPTER_14, TABLE_19

TEMPLATE_PATH = r"C:\Users\richa\OneDrive\Desktop\TOUR\MOBILE APPLICATION DEVELOPMENT - PBL DOCUMENTATION TEMPLATE.docx"
FIGURES_DIR = r"C:\Users\richa\OneDrive\Desktop\TOUR\TripDirect\report_figures"
OUTPUT_REPORT = r"C:\Users\richa\OneDrive\Desktop\TOUR\TripDirect\TripDirect_PBL_Report.docx"
OUTPUT_WORKING = r"C:\Users\richa\OneDrive\Desktop\TOUR\TripDirect\TripDirect_PBL_Report_Working.docx"

# Color constants
COLOR_NAVY = "1E3A8A"       # Primary header navy
COLOR_LIGHT_BG = "F8FAFC"   # Alternating row background
COLOR_BORDER = "CBD5E1"     # Table border gray
RGB_NAVY = RGBColor(0x1E, 0x3A, 0x8A)
RGB_DARK = RGBColor(0x0F, 0x17, 0x2A)
RGB_GRAY = RGBColor(0x47, 0x55, 0x69)

def style_paragraph(p, font_name="Times New Roman", size_pt=12, bold=False, italic=False, 
                    color=RGB_DARK, align=WD_ALIGN_PARAGRAPH.JUSTIFY, space_before=0, space_after=4, line_spacing=1.15):
    p.alignment = align
    p.paragraph_format.space_before = Pt(space_before)
    p.paragraph_format.space_after = Pt(space_after)
    p.paragraph_format.line_spacing = line_spacing
    for r in p.runs:
        r.font.name = font_name
        r.font.size = Pt(size_pt)
        r.bold = bold
        r.italic = italic
        r.font.color.rgb = color

def add_styled_paragraph(doc, text="", font_name="Times New Roman", size_pt=12, bold=False, italic=False,
                         color=RGB_DARK, align=WD_ALIGN_PARAGRAPH.JUSTIFY, space_before=0, space_after=4, line_spacing=1.15):
    p = doc.add_paragraph()
    p.alignment = align
    p.paragraph_format.space_before = Pt(space_before)
    p.paragraph_format.space_after = Pt(space_after)
    p.paragraph_format.line_spacing = line_spacing
    if text:
        r = p.add_run(text)
        r.font.name = font_name
        r.font.size = Pt(size_pt)
        r.bold = bold
        r.italic = italic
        r.font.color.rgb = color
    return p

def add_heading_1(doc, text, is_front_matter=False):
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.paragraph_format.space_before = Pt(10)
    p.paragraph_format.space_after = Pt(8)
    p.paragraph_format.keep_with_next = True
    r = p.add_run(text)
    r.font.name = "Times New Roman"
    r.font.size = Pt(14)
    r.bold = True
    r.font.color.rgb = RGB_NAVY
    return p

def add_heading_2(doc, text):
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.LEFT
    p.paragraph_format.space_before = Pt(8)
    p.paragraph_format.space_after = Pt(3)
    p.paragraph_format.keep_with_next = True
    r = p.add_run(text)
    r.font.name = "Times New Roman"
    r.font.size = Pt(13)
    r.bold = True
    r.font.color.rgb = RGB_NAVY
    return p

def add_heading_3(doc, text):
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.LEFT
    p.paragraph_format.space_before = Pt(6)
    p.paragraph_format.space_after = Pt(2)
    p.paragraph_format.keep_with_next = True
    r = p.add_run(text)
    r.font.name = "Times New Roman"
    r.font.size = Pt(12)
    r.bold = True
    r.font.color.rgb = RGB_DARK
    return p

def add_table_with_style(doc, headers, rows, title=None, col_widths=None):
    if title:
        cp = doc.add_paragraph()
        cp.alignment = WD_ALIGN_PARAGRAPH.LEFT
        cp.paragraph_format.space_before = Pt(8)
        cp.paragraph_format.space_after = Pt(3)
        cp.paragraph_format.keep_with_next = True
        cr = cp.add_run(title)
        cr.font.name = "Times New Roman"
        cr.font.size = Pt(10.5)
        cr.bold = True
        cr.font.color.rgb = RGB_NAVY

    table = doc.add_table(rows=len(rows) + 1, cols=len(headers))
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = False

    # Header Row
    hdr_cells = table.rows[0].cells
    for c_idx, h_text in enumerate(headers):
        hdr_cells[c_idx].text = h_text
        p = hdr_cells[c_idx].paragraphs[0]
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p.paragraph_format.space_before = Pt(2)
        p.paragraph_format.space_after = Pt(2)
        for r in p.runs:
            r.font.name = "Times New Roman"
            r.font.size = Pt(9.0)
            r.bold = True
            r.font.color.rgb = RGBColor(0xFF, 0xFF, 0xFF)

    # Data Rows
    for r_idx, row_data in enumerate(rows):
        row_cells = table.rows[r_idx + 1].cells
        for c_idx, val in enumerate(row_data):
            row_cells[c_idx].text = str(val)
            p = row_cells[c_idx].paragraphs[0]
            p.alignment = WD_ALIGN_PARAGRAPH.LEFT
            p.paragraph_format.space_before = Pt(1.5)
            p.paragraph_format.space_after = Pt(1.5)
            p.paragraph_format.line_spacing = 1.05
            for r in p.runs:
                r.font.name = "Times New Roman"
                r.font.size = Pt(8.5)
                r.font.color.rgb = RGB_DARK

    # Apply XML styling to table rows & cells
    for i, row in enumerate(table.rows):
        trPr = row._tr.get_or_add_trPr()
        trPr.append(parse_xml(r'<w:cantSplit {}/>'.format(nsdecls('w'))))
        if i == 0:
            trPr.append(parse_xml(r'<w:tblHeader {}/>'.format(nsdecls('w'))))
        
        for c_idx, cell in enumerate(row.cells):
            tcPr = cell._tc.get_or_add_tcPr()
            # Padding
            tcMar = parse_xml(r'<w:tcMar {}><w:top w:w="70" w:type="dxa"/><w:bottom w:w="70" w:type="dxa"/><w:left w:w="100" w:type="dxa"/><w:right w:w="100" w:type="dxa"/></w:tcMar>'.format(nsdecls('w')))
            tcPr.append(tcMar)
            # Background
            if i == 0:
                shd = parse_xml(r'<w:shd {} w:fill="{}"/>'.format(nsdecls('w'), COLOR_NAVY))
                tcPr.append(shd)
            elif i % 2 == 1:
                shd = parse_xml(r'<w:shd {} w:fill="{}"/>'.format(nsdecls('w'), COLOR_LIGHT_BG))
                tcPr.append(shd)
            # Borders
            borders = parse_xml(r'<w:tcBorders {}><w:top w:val="single" w:sz="4" w:space="0" w:color="{}"/><w:bottom w:val="single" w:sz="4" w:space="0" w:color="{}"/><w:left w:val="single" w:sz="4" w:space="0" w:color="{}"/><w:right w:val="single" w:sz="4" w:space="0" w:color="{}"/></w:tcBorders>'.format(nsdecls('w'), COLOR_BORDER, COLOR_BORDER, COLOR_BORDER, COLOR_BORDER))
            tcPr.append(borders)

            # Apply column widths if provided
            if col_widths and c_idx < len(col_widths):
                cell.width = Inches(col_widths[c_idx])

    # Space after table
    sp = doc.add_paragraph()
    sp.paragraph_format.space_before = Pt(0)
    sp.paragraph_format.space_after = Pt(4)
    return table

def add_figure_with_caption(doc, fig_filename, fig_num_str, fig_caption_str, width_inches=5.0):
    fig_path = os.path.join(FIGURES_DIR, fig_filename)
    if not os.path.exists(fig_path):
        print(f"Warning: Figure file not found: {fig_path}")
        return

    # Image paragraph
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.paragraph_format.space_before = Pt(6)
    p.paragraph_format.space_after = Pt(2)
    p.paragraph_format.keep_with_next = True
    r = p.add_run()
    r.add_picture(fig_path, width=Inches(width_inches))

    # Caption paragraph
    cp = doc.add_paragraph()
    cp.alignment = WD_ALIGN_PARAGRAPH.CENTER
    cp.paragraph_format.space_before = Pt(1)
    cp.paragraph_format.space_after = Pt(8)
    cr = cp.add_run(f"{fig_num_str}: {fig_caption_str}")
    cr.font.name = "Times New Roman"
    cr.font.size = Pt(10)
    cr.bold = True
    cr.italic = True
    cr.font.color.rgb = RGB_NAVY

def setup_page_number_field(paragraph):
    paragraph.alignment = WD_ALIGN_PARAGRAPH.CENTER
    run = paragraph.add_run()
    fldChar1 = OxmlElement('w:fldChar')
    fldChar1.set(qn('w:fldCharType'), 'begin')
    instrText = OxmlElement('w:instrText')
    instrText.set(qn('xml:space'), 'preserve')
    instrText.text = "PAGE"
    fldChar2 = OxmlElement('w:fldChar')
    fldChar2.set(qn('w:fldCharType'), 'separate')
    fldChar3 = OxmlElement('w:fldChar')
    fldChar3.set(qn('w:fldCharType'), 'end')
    run._r.append(fldChar1)
    run._r.append(instrText)
    run._r.append(fldChar2)
    run._r.append(fldChar3)
    run.font.name = "Times New Roman"
    run.font.size = Pt(10)
    run.font.color.rgb = RGB_DARK

def build_pbl_report():
    print("Loading template:", TEMPLATE_PATH)
    doc = docx.Document(TEMPLATE_PATH)
    doc._body.clear_content()

    # Configure Section 1 (Front Matter)
    s1 = doc.sections[0]
    s1.page_width = Inches(8.27)   # A4
    s1.page_height = Inches(11.69) # A4
    s1.left_margin = Inches(1.0)
    s1.right_margin = Inches(1.0)
    s1.top_margin = Inches(1.0)
    s1.bottom_margin = Inches(1.0)
    s1.different_first_page_header_footer = True

    # Roman numerals for Section 1
    sectPr1 = s1._sectPr
    pgNumType1 = OxmlElement('w:pgNumType')
    pgNumType1.set(qn('w:fmt'), 'lowerRoman')
    pgNumType1.set(qn('w:start'), '1')
    sectPr1.append(pgNumType1)

    # Footer for Section 1 (roman page number, skip first page)
    f1 = s1.footer
    setup_page_number_field(f1.paragraphs[0])

    print("Building Front Matter...")

    # =========================================================
    # FRONT MATTER PAGE 1: TITLE PAGE
    # =========================================================
    add_styled_paragraph(doc, FRONT_MATTER["institution"], size_pt=14.5, bold=True, color=RGB_NAVY, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=10, space_after=2)
    add_styled_paragraph(doc, FRONT_MATTER["institution_sub"], size_pt=9.5, italic=True, color=RGB_GRAY, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=10)
    add_styled_paragraph(doc, FRONT_MATTER["department"], size_pt=11.5, bold=True, color=RGB_NAVY, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=20)
    
    add_styled_paragraph(doc, FRONT_MATTER["report_type"], size_pt=13, bold=True, color=RGB_DARK, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=8, space_after=2)
    add_styled_paragraph(doc, FRONT_MATTER["course"], size_pt=11.5, bold=True, color=RGB_NAVY, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=24)
    
    add_styled_paragraph(doc, FRONT_MATTER["title"], size_pt=14, bold=True, color=RGB_NAVY, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=12, space_after=20, line_spacing=1.2)
    
    add_styled_paragraph(doc, FRONT_MATTER["degree"], size_pt=10.5, italic=True, color=RGB_DARK, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=20, line_spacing=1.15)
    
    add_styled_paragraph(doc, "Submitted by", size_pt=10.5, bold=True, color=RGB_NAVY, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=4)
    for student in FRONT_MATTER["submitted_by"]:
        add_styled_paragraph(doc, student, size_pt=10.5, bold=True, color=RGB_DARK, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=2)
    
    add_styled_paragraph(doc, "Under the guidance of", size_pt=10.5, bold=True, color=RGB_NAVY, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=16, space_after=4)
    add_styled_paragraph(doc, FRONT_MATTER["guidance"]["mentor"], size_pt=10.5, bold=True, color=RGB_DARK, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=2)
    add_styled_paragraph(doc, FRONT_MATTER["guidance"]["designation"], size_pt=10.0, color=RGB_DARK, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=0, space_after=28)
    
    add_styled_paragraph(doc, FRONT_MATTER["date_place"], size_pt=10.5, bold=True, color=RGB_NAVY, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=8, space_after=0)

    # Page Break to Certificate
    doc.add_page_break()

    # =========================================================
    # FRONT MATTER PAGE 2: BONAFIDE CERTIFICATE
    # =========================================================
    add_heading_1(doc, "BONAFIDE CERTIFICATE", is_front_matter=True)
    add_styled_paragraph(doc, FRONT_MATTER["certificate"]["text"], size_pt=11.5, space_before=12, space_after=35, line_spacing=1.25)
    
    # Signature row for Mentor & HOD
    add_table_with_style(doc, ["Faculty Mentor", "Head of Department"], [
        ["\n\n________________________\nSignature of Faculty Mentor", "\n\n________________________\nSignature of Head of Department"],
        ["Date: ______________", "Date: ______________"]
    ], col_widths=[3.0, 3.0])

    add_styled_paragraph(doc, FRONT_MATTER["certificate"]["eval_text"], size_pt=11, bold=True, space_before=30, space_after=15)
    add_table_with_style(doc, ["Evaluation Committee", "Signatures"], [
        [FRONT_MATTER["certificate"]["mentor_sign"], "________________________"],
        [FRONT_MATTER["certificate"]["incharge_sign"], "________________________"]
    ], col_widths=[3.2, 2.8])

    doc.add_page_break()

    # =========================================================
    # FRONT MATTER PAGE 3: DECLARATION
    # =========================================================
    add_heading_1(doc, "DECLARATION", is_front_matter=True)
    add_styled_paragraph(doc, FRONT_MATTER["declaration"]["text"], size_pt=11.5, space_before=16, space_after=25, line_spacing=1.25)
    
    add_styled_paragraph(doc, "Team Members & Signatures:", size_pt=11.5, bold=True, space_before=8, space_after=8)
    add_table_with_style(doc, ["S.No.", "Team Member Name", "Register Number", "Signature"], [
        ["1.", "[Student Name 1]", "[Register No. 1]", "____________________"],
        ["2.", "[Student Name 2]", "[Register No. 2]", "____________________"]
    ], col_widths=[0.6, 2.4, 1.8, 1.8])

    doc.add_page_break()

    # =========================================================
    # FRONT MATTER PAGE 4: PBL COURSE AND TEAM DETAILS
    # =========================================================
    add_heading_1(doc, "PBL COURSE AND TEAM DETAILS", is_front_matter=True)
    add_table_with_style(doc, PBL_TABLE_0["headers"], PBL_TABLE_0["rows"], title=PBL_TABLE_0["title"], col_widths=[2.0, 4.5])
    
    add_heading_2(doc, "Team Roles and Responsibilities")
    add_table_with_style(doc, PBL_TABLE_1["headers"], PBL_TABLE_1["rows"], title=PBL_TABLE_1["title"], col_widths=[1.5, 1.2, 1.8, 2.2])

    doc.add_page_break()

    # =========================================================
    # FRONT MATTER PAGE 5: ACKNOWLEDGEMENT
    # =========================================================
    add_heading_1(doc, "ACKNOWLEDGEMENT", is_front_matter=True)
    for p_ack in FRONT_MATTER["acknowledgement"].split("\n\n"):
        add_styled_paragraph(doc, p_ack, size_pt=11.5, space_before=6, space_after=10, line_spacing=1.2)

    doc.add_page_break()

    # =========================================================
    # FRONT MATTER PAGE 6: ABSTRACT AND KEYWORDS
    # =========================================================
    add_heading_1(doc, "ABSTRACT", is_front_matter=True)
    for p_abs in FRONT_MATTER["abstract"].split("\n\n"):
        add_styled_paragraph(doc, p_abs, size_pt=11.5, space_before=6, space_after=10, line_spacing=1.2)
    
    kp = doc.add_paragraph()
    kp.paragraph_format.space_before = Pt(10)
    kp.paragraph_format.space_after = Pt(12)
    kr1 = kp.add_run("Keywords: ")
    kr1.font.name = "Times New Roman"
    kr1.font.size = Pt(11.5)
    kr1.bold = True
    kr1.font.color.rgb = RGB_NAVY
    kr2 = kp.add_run(FRONT_MATTER["keywords"])
    kr2.font.name = "Times New Roman"
    kr2.font.size = Pt(11.5)
    kr2.italic = True

    doc.add_page_break()

    # =========================================================
    # FRONT MATTER PAGES 7-9: TABLE OF CONTENTS
    # =========================================================
    add_heading_1(doc, "TABLE OF CONTENTS", is_front_matter=True)
    
    toc_front_headers = ["TITLE / SECTION", "PAGE NO."]
    toc_front_rows = [
        ["BONAFIDE CERTIFICATE", "ii"],
        ["DECLARATION", "iii"],
        ["PBL COURSE AND TEAM DETAILS", "iv"],
        ["ACKNOWLEDGEMENT", "v"],
        ["ABSTRACT AND KEYWORDS", "vi"],
        ["TABLE OF CONTENTS", "vii"],
        ["LIST OF FIGURES", "xi"],
        ["LIST OF TABLES", "xii"],
        ["LIST OF ABBREVIATIONS", "xiii"]
    ]
    add_table_with_style(doc, toc_front_headers, toc_front_rows, col_widths=[5.2, 1.2])

    toc_chapters = [
        ("CHAPTER 1 – INTRODUCTION", [
            ("1.1 Project Overview", "1"),
            ("1.2 Problem Statement", "2"),
            ("1.3 Objectives of the Project", "2"),
            ("1.4 Scope of the Project", "3"),
            ("1.5 Target Users", "4"),
            ("1.6 Development Platform and Technologies", "4")
        ]),
        ("CHAPTER 2 – LITERATURE SURVEY", [
            ("2.1 Related Existing Mobile Applications", "5"),
            ("2.2 Review of Existing Technologies", "5"),
            ("2.3 Review of Mobile Application Frameworks", "6"),
            ("2.4 Comparison of Existing Applications (Table 2.3)", "6"),
            ("2.5 Research Gap / Identified Limitations", "6")
        ]),
        ("CHAPTER 3 – EXISTING SYSTEM", [
            ("3.1 Description of Existing System", "9"),
            ("3.2 Existing Application Workflow", "9"),
            ("3.3 Technologies Used in Existing Systems", "9"),
            ("3.4 Drawbacks and Limitations", "10"),
            ("Table 3.1: Weekly PBL Progress Log", "10")
        ]),
        ("CHAPTER 4 – PROPOSED SYSTEM", [
            ("4.1 Proposed Solution", "12"),
            ("4.2 Features of the Proposed Application", "12"),
            ("4.3 Functional Requirements (Table 4.1)", "13"),
            ("4.4 Non-Functional Requirements (Table 4.2)", "13"),
            ("4.5 Advantages of the Proposed Application", "13")
        ]),
        ("CHAPTER 5 – SYSTEM ANALYSIS AND DESIGN", [
            ("5.1 System Architecture / Block Diagram (Figure 5.1)", "16"),
            ("5.1.1 Physical Design (Figure 5.1.1)", "16"),
            ("5.1.2 Logical Design (Figure 5.1.2)", "17"),
            ("5.2 Working Principle", "18"),
            ("5.3 UML / Use Case Diagram (Figure 5.2)", "19"),
            ("5.4 Activity Diagram (Figure 5.3)", "19"),
            ("5.5 Class Diagram (Figure 5.5)", "20"),
            ("5.6 Sequence Diagram (Figure 5.6)", "21"),
            ("5.7 Data Flow Diagram (DFD Level 0 & Level 1) (Figure 5.7)", "22"),
            ("5.8 Entity Relationship (ER) Diagram (Figure 5.8)", "23"),
            ("5.9 Application Flowchart (Figure 5.4)", "24"),
            ("5.10 Navigation Flow / Screen Flow (Figure 5.9)", "25")
        ]),
        ("CHAPTER 6 – DEVELOPMENT TOOLS AND TECHNOLOGIES", [
            ("6.1 Development Environment & Antigravity IDE (Figure 6.1)", "27"),
            ("6.2 Programming Language – Kotlin", "27"),
            ("6.3 User Interface Design – Jetpack Compose", "28"),
            ("6.4 Backend Technology – FastAPI", "28"),
            ("6.5 Database Technology – SQLite & SQLAlchemy", "28"),
            ("6.6 APIs and Web Services (OSM, Overpass, Meteo, OSRM)", "28"),
            ("6.7 Version Control – Git / GitHub", "29"),
            ("6.8 Emulator / Physical Device Testing", "29"),
            ("6.9 Technology Stack Architecture (Figure 6.2)", "29"),
            ("Table 6.1: Software Development Tools", "29"),
            ("Table 6.2: Hardware and Software Requirements", "29")
        ]),
        ("CHAPTER 7 – MOBILE APPLICATION IMPLEMENTATION", [
            ("7.1 Application Setup and Configuration (Figure 7.1)", "32"),
            ("7.2 Project Structure & Package Hierarchy", "32"),
            ("7.3 User Interface Design & Declarative UI (Figure 7.2)", "32"),
            ("7.4 User Registration and Login", "33"),
            ("7.5 Home Screen / Dashboard", "33"),
            ("7.6 Application Modules (Table 7.1)", "33"),
            ("7.7 Database Implementation (Figure 7.3, Table 7.2)", "33"),
            ("7.8 API Integration (Figure 7.4, Table 7.3)", "34"),
            ("7.9 Authentication and Authorization", "34"),
            ("7.10 Notifications and Alerts", "34"),
            ("7.11 Data Validation and Error Handling", "35"),
            ("7.12 Source Code Implementation (Figure 7.5)", "35")
        ]),
        ("CHAPTER 8 – USER INTERFACE AND USER EXPERIENCE", [
            ("8.1 UI/UX Design Principles", "39"),
            ("8.2 Application Theme and Layout", "39"),
            ("8.3 Screen Design (Table 8.1)", "39"),
            ("8.4 Navigation Design (Figure 8.4)", "39"),
            ("8.5 Input Forms and Validation", "40"),
            ("8.6 Responsive Design & Accessibility", "40"),
            ("8.7 Screenshots of Application Interfaces (Figs 8.1-8.5)", "40")
        ]),
        ("CHAPTER 9 – TESTING AND RESULTS", [
            ("9.1 Testing Strategy (Figure 9.1)", "44"),
            ("9.2 Unit Testing (107 Pytest cases & Domain tests)", "44"),
            ("9.3 Integration Testing (Live Ooty execution)", "45"),
            ("9.4 Functional Testing (Tables 9.1 & 9.2)", "45"),
            ("9.5 UI Testing & Test Execution Results (Figure 9.2)", "45"),
            ("9.6 Test Cases and Execution Results", "45"),
            ("9.7 Experimental Results (Table 9.3)", "45"),
            ("9.8 Output Screenshots (Figure 9.4)", "46"),
            ("9.9 Error Handling and Validation Results", "46"),
            ("9.10 Performance Analysis (Figure 9.3 & Table 9.5)", "46"),
            ("9.11 User Acceptance Testing & Final Execution (Figure 9.5)", "47")
        ]),
        ("CHAPTER 10 – APPLICATIONS", [
            ("10.1 Application Areas & Real-World Uses", "50"),
            ("10.2 Target User Groups & Industry Value", "50")
        ]),
        ("CHAPTER 11 – ADVANTAGES AND LIMITATIONS", [
            ("11.1 Advantages of TripDirect", "51"),
            ("11.2 Limitations & Security Considerations", "51"),
            ("11.3 Performance Constraints", "51")
        ]),
        ("CHAPTER 12 – CONCLUSION AND FUTURE SCOPE", [
            ("12.1 Conclusion & Project Outcomes", "52"),
            ("12.2 Future Enhancements (Live Booking, Collab, Voice)", "52")
        ]),
        ("CHAPTER 13 – REFERENCES", [
            ("IEEE Numbered References [1] - [18]", "53")
        ]),
        ("CHAPTER 14 – APPENDIX", [
            ("14.1 Selected Source Code Excerpts", "54"),
            ("14.2 Database Schema DDL Specification", "54"),
            ("14.3 REST API Documentation Specification", "54"),
            ("14.4 UI Design Screens and Wireframes", "55"),
            ("14.5 Test Case Details and Pytest Verification", "55"),
            ("14.6 Additional Screenshots and Media", "55"),
            ("14.7 Installation and Deployment Procedure", "55"),
            ("Table 19.1: Self and Peer Assessment of Contribution", "55")
        ])
    ]

    for ch_title, ch_sections in toc_chapters:
        add_table_with_style(doc, [ch_title, "PAGE NO."], ch_sections, col_widths=[5.2, 1.2])

    doc.add_page_break()

    # =========================================================
    # FRONT MATTER PAGE 11: LIST OF FIGURES
    # =========================================================
    add_heading_1(doc, "LIST OF FIGURES", is_front_matter=True)
    list_of_figures_data = [
        ["Figure 5.1", "System Architecture / Block Diagram of TripDirect", "16"],
        ["Figure 5.1.1", "Physical Architecture / Deployment Design", "17"],
        ["Figure 5.1.2", "Logical Architecture / Application Module Design", "18"],
        ["Figure 5.2", "TripDirect UML Use Case Diagram", "19"],
        ["Figure 5.3", "TripDirect End-to-End Activity Diagram", "20"],
        ["Figure 5.4", "Mobile Application Operational Flowchart", "25"],
        ["Figure 5.5", "TripDirect Architecture Class Diagram", "21"],
        ["Figure 5.6", "Sequence Diagram of AI Trip Planning Workflow", "22"],
        ["Figure 5.7", "Data Flow Diagram (DFD Context Level 0 & Level 1 Decomposition)", "23"],
        ["Figure 5.8", "Entity Relationship (ER) Diagram of Database Architecture", "24"],
        ["Figure 5.9", "Application Screen Navigation Flow", "26"],
        ["Figure 6.1", "Mobile Application Development Platform & Toolchain", "27"],
        ["Figure 6.2", "TripDirect Full-Stack Technology Architecture", "29"],
        ["Figure 7.1", "Application Package and Source Code Hierarchy", "32"],
        ["Figure 7.2", "Jetpack Compose Declarative UI Architecture", "33"],
        ["Figure 7.3", "Application Database Configuration & SQLAlchemy Mapping", "34"],
        ["Figure 7.4", "REST API Integration and Network Serialization Pipeline", "34"],
        ["Figure 7.5", "Application Implementation and Processing Workflow", "36"],
        ["Figure 8.1", "TripDirect User Authentication and Onboarding Screens", "41"],
        ["Figure 8.2", "TripDirect Home Screen and Destination Discovery Dashboard", "41"],
        ["Figure 8.3", "TripDirect Prototype Development and Component Engineering", "42"],
        ["Figure 8.4", "Application Bottom Navigation and Multi-Stack Screen Flow", "40"],
        ["Figure 8.5", "TripDirect Feature Interfaces: AI Planner, Budget Calculator, and SOS Safety", "42"],
        ["Figure 9.1", "Systematic Software Verification and Testing Workflow", "44"],
        ["Figure 9.2", "Pytest Automated Test Execution Results by Module (100% Pass Rate)", "45"],
        ["Figure 9.3", "TripDirect API Latency and Benchmark Performance Analysis", "47"],
        ["Figure 9.4", "TripDirect Functional Test Execution Matrix", "46"],
        ["Figure 9.5", "Final Mobile Application Verification and Execution Summary", "47"]
    ]
    add_table_with_style(doc, ["FIGURE NO.", "FIGURE TITLE", "PAGE NO."], list_of_figures_data, col_widths=[1.5, 4.2, 1.0])

    doc.add_page_break()

    # =========================================================
    # FRONT MATTER PAGE 12: LIST OF TABLES
    # =========================================================
    add_heading_1(doc, "LIST OF TABLES", is_front_matter=True)
    list_of_tables_data = [
        ["Table 0.1", "PBL Course and Team Details", "iv"],
        ["Table 0.2", "Team Roles and Responsibilities", "iv"],
        ["Table 2.3", "Comparison of Existing Mobile Applications and Systems", "6"],
        ["Table 3.1", "Weekly Project-Based Learning (PBL) Progress Log", "10"],
        ["Table 4.1", "Functional Requirements of TripDirect", "13"],
        ["Table 4.2", "Non-Functional Requirements of TripDirect", "13"],
        ["Table 6.1", "Software Development Tools and Technologies", "29"],
        ["Table 6.2", "Hardware and Software Requirements for Application Development", "29"],
        ["Table 7.1", "Mobile Application Modules and Their Functionalities", "33"],
        ["Table 7.2", "Database Tables and Data Entities", "33"],
        ["Table 7.3", "REST API and Web Service Endpoint Details", "34"],
        ["Table 8.1", "Application Screens and Their Functionalities", "39"],
        ["Table 9.1", "Test Cases and Expected Results", "45"],
        ["Table 9.2", "Test Case Execution Results (107 Tests)", "45"],
        ["Table 9.3", "Experimental Result Comparison", "45"],
        ["Table 9.5", "Performance Analysis of the Mobile Application", "46"],
        ["Table 19.1", "Self and Peer Assessment of Contribution", "55"]
    ]
    add_table_with_style(doc, ["TABLE NO.", "TABLE TITLE", "PAGE NO."], list_of_tables_data, col_widths=[1.4, 4.3, 1.0])

    doc.add_page_break()

    # =========================================================
    # FRONT MATTER PAGE 12: LIST OF ABBREVIATIONS
    # =========================================================
    add_heading_1(doc, "LIST OF ABBREVIATIONS", is_front_matter=True)
    add_table_with_style(doc, ["Abbreviation", "Expanded Definition / Term"], ABBREVIATIONS, col_widths=[1.8, 4.8])

    # =========================================================
    # SECTION BREAK: SWITCH TO DECIMAL NUMBERING FOR CHAPTERS
    # =========================================================
    s2 = doc.add_section(WD_SECTION.NEW_PAGE)
    s2.different_first_page_header_footer = False
    s2.header.is_linked_to_previous = False
    s2.footer.is_linked_to_previous = False

    # Decimal numerals starting at 1
    sectPr2 = s2._sectPr
    pgNumType2 = OxmlElement('w:pgNumType')
    pgNumType2.set(qn('w:fmt'), 'decimal')
    pgNumType2.set(qn('w:start'), '1')
    sectPr2.append(pgNumType2)

    # Footer for Section 2 (decimal page number centered)
    f2 = s2.footer
    setup_page_number_field(f2.paragraphs[0])

    print("Building Chapters 1 to 14...")

    # Helper function to render a chapter
    def render_chapter(ch_data):
        ch_num = ch_data["num"]
        ch_title = ch_data["title"]
        print(f"  Rendering CHAPTER {ch_num}: {ch_title}...")
        add_heading_1(doc, f"CHAPTER {ch_num}\n{ch_title}")
        
        for sec in ch_data.get("sections", []):
            sec_id = sec["id"]
            sec_title = sec["title"]
            if sec_id.count('.') == 1:
                add_heading_2(doc, f"{sec_id} {sec_title}")
            else:
                add_heading_3(doc, f"{sec_id} {sec_title}")

            for p_text in sec.get("content", []):
                add_styled_paragraph(doc, p_text, size_pt=11.5, space_before=2, space_after=4, line_spacing=1.15)

            # Check for embedded figure
            if "figure" in sec:
                fig = sec["figure"]
                add_figure_with_caption(doc, fig["file"], fig["id"], fig["title"], width_inches=4.8)

            # Check for multiple figures
            if "figures" in sec:
                for fig in sec["figures"]:
                    add_figure_with_caption(doc, fig["file"], fig["id"], fig["title"], width_inches=4.8)

    # ---------------- CHAPTER 1 ----------------
    render_chapter(CHAPTER_1)
    doc.add_page_break()

    # ---------------- CHAPTER 2 ----------------
    render_chapter(CHAPTER_2)
    add_table_with_style(doc, TABLE_2_3["headers"], TABLE_2_3["rows"], title=TABLE_2_3["title"])
    doc.add_page_break()

    # ---------------- CHAPTER 3 ----------------
    render_chapter(CHAPTER_3)
    add_table_with_style(doc, TABLE_3_1["headers"], TABLE_3_1["rows"], title=TABLE_3_1["title"], col_widths=[1.0, 1.6, 3.2, 1.4])
    doc.add_page_break()

    # ---------------- CHAPTER 4 ----------------
    render_chapter(CHAPTER_4)
    add_table_with_style(doc, TABLE_4_1["headers"], TABLE_4_1["rows"], title=TABLE_4_1["title"], col_widths=[0.9, 1.6, 3.5, 1.2])
    add_table_with_style(doc, TABLE_4_2["headers"], TABLE_4_2["rows"], title=TABLE_4_2["title"], col_widths=[1.3, 2.5, 1.5, 1.8])
    doc.add_page_break()

    # ---------------- CHAPTER 5 ----------------
    render_chapter(CHAPTER_5)
    doc.add_page_break()

    # ---------------- CHAPTER 6 ----------------
    render_chapter(CHAPTER_6)
    add_table_with_style(doc, TABLE_6_1["headers"], TABLE_6_1["rows"], title=TABLE_6_1["title"], col_widths=[1.4, 1.0, 1.6, 3.0])
    add_table_with_style(doc, TABLE_6_2["headers"], TABLE_6_2["rows"], title=TABLE_6_2["title"], col_widths=[1.5, 1.8, 2.0, 2.0])
    doc.add_page_break()

    # ---------------- CHAPTER 7 ----------------
    render_chapter(CHAPTER_7)
    add_table_with_style(doc, TABLE_7_1["headers"], TABLE_7_1["rows"], title=TABLE_7_1["title"], col_widths=[1.3, 1.8, 1.5, 2.5, 1.0])
    add_table_with_style(doc, TABLE_7_2["headers"], TABLE_7_2["rows"], title=TABLE_7_2["title"], col_widths=[1.3, 1.0, 1.5, 2.5, 1.8])
    add_table_with_style(doc, TABLE_7_3["headers"], TABLE_7_3["rows"], title=TABLE_7_3["title"], col_widths=[0.8, 1.8, 2.2, 1.8, 1.0])
    doc.add_page_break()

    # ---------------- CHAPTER 8 ----------------
    render_chapter(CHAPTER_8)
    add_table_with_style(doc, TABLE_8_1["headers"], TABLE_8_1["rows"], title=TABLE_8_1["title"], col_widths=[1.4, 2.0, 1.6, 2.2])
    doc.add_page_break()

    # ---------------- CHAPTER 9 ----------------
    render_chapter(CHAPTER_9)
    add_table_with_style(doc, TABLE_9_1["headers"], TABLE_9_1["rows"], title=TABLE_9_1["title"], col_widths=[1.2, 1.3, 2.5, 2.5])
    add_table_with_style(doc, TABLE_9_2["headers"], TABLE_9_2["rows"], title=TABLE_9_2["title"], col_widths=[2.5, 0.8, 0.8, 0.8, 1.0, 1.1])
    add_table_with_style(doc, TABLE_9_3["headers"], TABLE_9_3["rows"], title=TABLE_9_3["title"], col_widths=[2.0, 2.0, 2.0, 1.5])
    add_table_with_style(doc, TABLE_9_5["headers"], TABLE_9_5["rows"], title=TABLE_9_5["title"], col_widths=[1.8, 1.8, 1.2, 1.5, 1.3])
    doc.add_page_break()

    # ---------------- CHAPTER 10 ----------------
    render_chapter(CHAPTER_10)
    doc.add_page_break()

    # ---------------- CHAPTER 11 ----------------
    render_chapter(CHAPTER_11)
    doc.add_page_break()

    # ---------------- CHAPTER 12 ----------------
    render_chapter(CHAPTER_12)
    doc.add_page_break()

    # ---------------- CHAPTER 13 ----------------
    add_heading_1(doc, f"CHAPTER 13\n{CHAPTER_13['title']}")
    add_styled_paragraph(doc, "All citations follow the standard IEEE numbered reference format, ordered sequentially by appearance in the text:", size_pt=11.5, space_before=3, space_after=8)
    for ref_str in CHAPTER_13["references"]:
        rp = doc.add_paragraph()
        rp.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
        rp.paragraph_format.space_before = Pt(2)
        rp.paragraph_format.space_after = Pt(4)
        rp.paragraph_format.line_spacing = 1.15
        rr = rp.add_run(ref_str)
        rr.font.name = "Times New Roman"
        rr.font.size = Pt(10.0)
        rr.font.color.rgb = RGB_DARK
    doc.add_page_break()

    # ---------------- CHAPTER 14 ----------------
    render_chapter(CHAPTER_14)
    add_table_with_style(doc, TABLE_19["headers"], TABLE_19["rows"], title=TABLE_19["title"], col_widths=[1.5, 1.8, 1.0, 1.0, 2.5])

    print("Saving document to:", OUTPUT_REPORT)
    doc.save(OUTPUT_REPORT)
    print("Saving working copy to:", OUTPUT_WORKING)
    doc.save(OUTPUT_WORKING)
    print("Successfully built calibrated TripDirect PBL Report!")

if __name__ == '__main__':
    build_pbl_report()

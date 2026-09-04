(function () {
    "use strict";

    function initAcademicProgramSelector() {
        const level = document.getElementById("academicLevel");
        const program = document.getElementById("academicProgramId");
        const button = document.getElementById("generateButton");
        const credentialRadios = Array.from(
            document.querySelectorAll(".credential-configuration-id"),
        );

        if (!level || !program) {
            return;
        }

        const programOptions = Array.from(
            program.querySelectorAll("option[data-level]"),
        );

        function updateButton() {
            if (!button) {
                return;
            }

            button.disabled = !(
                level.value &&
                program.value &&
                credentialRadios.some((radio) => radio.checked)
            );
        }

        function updatePrograms() {
            const selectedLevel = (level.value || "").trim().toUpperCase();

            program.value = "";

            programOptions.forEach((option) => {
                const optionLevel = (option.dataset.level || "")
                    .trim()
                    .toUpperCase();
                const matches = selectedLevel !== "" && optionLevel === selectedLevel;

                option.hidden = !matches;
                option.disabled = !matches;
            });

            const hasPrograms = programOptions.some((option) => {
                const optionLevel = (option.dataset.level || "")
                    .trim()
                    .toUpperCase();
                return optionLevel === selectedLevel;
            });

            program.disabled = selectedLevel === "" || !hasPrograms;
            program.selectedIndex = 0;
            program.options[0].textContent = selectedLevel
                ? (hasPrograms ? "Seleccione un programa" : "No hay programas para este nivel")
                : "Seleccione primero el nivel académico";

            updateButton();
        }

        level.addEventListener("change", updatePrograms);
        program.addEventListener("change", updateButton);
        credentialRadios.forEach((radio) =>
            radio.addEventListener("change", updateButton),
        );

        updatePrograms();
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", initAcademicProgramSelector);
    } else {
        initAcademicProgramSelector();
    }
})();

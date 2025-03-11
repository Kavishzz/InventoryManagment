import React from "react";

const PaginationComponent = ({crrentPage, totalPages, onPageChange}) => {
    const pageNumbers = Array.from({length: totalPages}, (_, i) => i+1);

    return(
        <div className="pagination-container">
            <button className="pagination-button"
            disabled={crrentPage === 1}
            onClick={() => onPageChange(crrentPage - 1)}>
                &laquo; Prev
            </button>

            {pageNumbers.map((number) => (
                <button key={number}
                className={`pagination-button ${crrentPage == number ? "active": ""} ` }
                onClick={() => onPageChange(number)}>
                {number}
                </button>
            ))}             

            <button className="pagination-container"
            disabled={crrentPage === totalPages}
            onClick={() => onPageChange(crrentPage + 1)}>
                Next &raquo;
            </button>
        </div>
    )
}

export default PaginationComponent;